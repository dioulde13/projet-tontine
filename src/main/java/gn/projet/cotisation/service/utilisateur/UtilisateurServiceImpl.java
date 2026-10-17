package gn.projet.cotisation.service.utilisateur;

import gn.projet.cotisation.dto.mapper.UtilisateurMapper; // conversion Entité -> DTO
import gn.projet.cotisation.dto.utilisateurs.CreateMembreRequestDTO; // DTO d'entrée pour créer un membre
import gn.projet.cotisation.dto.utilisateurs.UpdateUtilisateurRequestDTO; // DTO d'entrée
import gn.projet.cotisation.dto.utilisateurs.UtilisateurResponseDTO; // DTO de sortie
import gn.projet.cotisation.entity.Role; // entité JPA Role (pour les rôles attribués)
import gn.projet.cotisation.entity.Utilisateur; // entité JPA Utilisateur
import gn.projet.cotisation.entity.Village; // entité JPA Village (pour village / villagesGeres)
import gn.projet.cotisation.exception.role.RoleNotFoundException; // levée si le rôle MEMBRE n'existe pas
import gn.projet.cotisation.exception.utilisateur.TelephoneDejaUtiliseException; // levée si téléphone déjà pris
import gn.projet.cotisation.exception.utilisateur.UtilisateurNotFoundException; // levée si id absent
import gn.projet.cotisation.exception.village.VillageNotFoundException; // levée si villageId invalide
import gn.projet.cotisation.repository.role.RoleRepository; // pour résoudre les roleIds en entités Role
import gn.projet.cotisation.repository.utilisateur.UtilisateurRepository; // accès base pour Utilisateur
import gn.projet.cotisation.repository.village.VillageRepository; // pour résoudre villageId/villagesGeresIds
import gn.projet.cotisation.service.village.VillageAccessService; // vérification centralisée "village géré par ce président ?"
import lombok.extern.slf4j.Slf4j; // génère automatiquement le champ "log" utilisé plus bas
import org.springframework.security.crypto.password.PasswordEncoder; // hache le mot de passe avant stockage
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service // enregistre cette classe comme bean Spring, injectable dans UtilisateurController
public class UtilisateurServiceImpl implements UtilisateurService {

    // Nom du rôle attribué automatiquement par createMembre() — jamais choisi par le client
    private static final String ROLE_MEMBRE = "MEMBRE";

    private final UtilisateurRepository utilisateurRepository; // accès CRUD sur les utilisateurs
    // Nécessaire pour retrouver les Role existants à partir des roleIds envoyés dans le DTO de requête
    private final RoleRepository roleRepository;
    // Nécessaire pour retrouver les Village existants à partir de villageId / villagesGeresIds
    private final VillageRepository villageRepository;
    // Composant partagé : vérifie "ce village existe-t-il, et ce président le gère-t-il ?"
    // (mutualisé avec toute future fonctionnalité scopée par village, pas dupliqué ici)
    private final VillageAccessService villageAccessService;
    // Injecté depuis le bean déclaré dans SecurityConfig (BCryptPasswordEncoder) ; sert à hacher
    // le mot de passe AVANT de le sauvegarder — jamais stocké en clair en base.
    private final PasswordEncoder passwordEncoder;

    // Spring injecte automatiquement les repositories, VillageAccessService et le PasswordEncoder
    // via ce constructeur au démarrage
    public UtilisateurServiceImpl(
            UtilisateurRepository utilisateurRepository,
            RoleRepository roleRepository,
            VillageRepository villageRepository,
            VillageAccessService villageAccessService,
            PasswordEncoder passwordEncoder
    ) {
        this.utilisateurRepository = utilisateurRepository; // on garde la référence dans le champ
        this.roleRepository = roleRepository; // idem
        this.villageRepository = villageRepository; // idem
        this.villageAccessService = villageAccessService; // idem
        this.passwordEncoder = passwordEncoder; // idem
    }

    @Override
    public List<UtilisateurResponseDTO> findAll() {
        return utilisateurRepository.findAll().stream() // toutes les entités Utilisateur, en flux
                .map(UtilisateurMapper::toResponseDto) // chaque entité -> son DTO de réponse
                .toList(); // flux -> List
    }

    @Override
    public UtilisateurResponseDTO findById(Long id) {
        Utilisateur utilisateur = utilisateurRepository.findById(id) // cherche par id, Optional<Utilisateur>
                .orElseThrow(() -> new UtilisateurNotFoundException(id)); // stoppe ici si absent
        return UtilisateurMapper.toResponseDto(utilisateur); // conversion en DTO pour la réponse
    }

    @Override
    public UtilisateurResponseDTO create(UpdateUtilisateurRequestDTO dto) {
        // Vérification explicite AVANT d'essayer d'enregistrer : on préfère lever une exception
        // métier claire (TelephoneDejaUtiliseException -> 409) plutôt que de laisser MySQL rejeter
        // l'insertion et récupérer une DataIntegrityViolationException moins précise.
        checkTelephoneDisponible(dto.telephone(), null); // null = pas d'utilisateur à exclure (création, pas de "soi-même")

        Utilisateur utilisateur = new Utilisateur(); // nouvelle entité vide, id encore null
        utilisateur.setNom(dto.nom()); // copie chaque champ du DTO reçu vers l'entité
        utilisateur.setPrenom(dto.prenom());
        utilisateur.setTelephone(dto.telephone());
        // Le mot de passe initial est TOUJOURS le numéro de téléphone, jamais une valeur choisie par le
        // client : .encode() transforme ce téléphone en un hash BCrypt irréversible (avec sel intégré) —
        // c'est CE hash qui est stocké en base, jamais le téléphone ou un mot de passe en clair.
        utilisateur.setMotDePasse(passwordEncoder.encode(dto.telephone()));
        utilisateur.setRoles(resolveRoles(dto.roleIds())); // transforme les roleIds en vraies entités Role
        utilisateur.setVillage(resolveVillage(dto.villageId())); // village d'appartenance si MEMBRE, sinon null
        utilisateur.setVillagesGeres(resolveVillages(dto.villagesGeresIds())); // villages gérés si PRESIDENT
        Utilisateur saved = utilisateurRepository.save(utilisateur); // INSERT en base ; "saved" a son id généré
        return UtilisateurMapper.toResponseDto(saved); // DTO de réponse (sans motDePasse)
    }

    @Override
    public UtilisateurResponseDTO update(Long id, UpdateUtilisateurRequestDTO dto) {
        Utilisateur utilisateur = utilisateurRepository.findById(id) // charge l'utilisateur à modifier
                .orElseThrow(() -> new UtilisateurNotFoundException(id)); // stoppe ici si l'id n'existe pas

        checkTelephoneDisponible(dto.telephone(), id); // id = exclure CET utilisateur du contrôle d'unicité

        // Le téléphone sert aussi de mot de passe : s'il change, on resynchronise le mot de passe sur le
        // nouveau numéro (même règle qu'à la création), plutôt que de le laisser pointer sur l'ancien numéro.
        boolean telephoneChange = !dto.telephone().equals(utilisateur.getTelephone());

        utilisateur.setNom(dto.nom()); // on écrase chaque champ avec les nouvelles valeurs envoyées
        utilisateur.setPrenom(dto.prenom());
        utilisateur.setTelephone(dto.telephone());
        if (telephoneChange) {
            utilisateur.setMotDePasse(passwordEncoder.encode(dto.telephone())); // resynchronise sur le nouveau numéro
        } else if (dto.motDePasse() != null) {
            utilisateur.setMotDePasse(passwordEncoder.encode(dto.motDePasse())); // sinon, mot de passe choisi explicitement
        }
        utilisateur.setRoles(resolveRoles(dto.roleIds())); // remplace entièrement l'ensemble de rôles
        utilisateur.setVillage(resolveVillage(dto.villageId())); // remplace le village d'appartenance
        utilisateur.setVillagesGeres(resolveVillages(dto.villagesGeresIds())); // remplace les villages gérés
        return UtilisateurMapper.toResponseDto(utilisateurRepository.save(utilisateur)); // UPDATE, puis DTO
    }

    @Override
    public UtilisateurResponseDTO createMembre(Long villageId, CreateMembreRequestDTO dto, Utilisateur presidentConnecte) {
        // "presidentConnecte" est arrivé en PARAMÈTRE de cette méthode : c'est VillageMembreController.create()
        // qui l'a passé, lui-même l'ayant reçu via @AuthenticationPrincipal (donc, in fine, extrait du token JWT).
        log.info("createMembre() : villageId={}, telephone demandé={}, president={}",
                villageId, dto.telephone(), presidentConnecte.getTelephone());

        Village village = villageAccessService.verifierEtRecupererVillageGere(villageId, presidentConnecte);
        log.info("Accès autorisé : {} gère bien le village {}", presidentConnecte.getTelephone(), village.getNom());

        checkTelephoneDisponible(dto.telephone(), null); // même contrôle d'unicité que pour create()

        // Le rôle MEMBRE est cherché par son nom (pas par un id fourni par le client) : contrairement à
        // create() où le client choisit librement roleIds, ici le rôle est TOUJOURS MEMBRE, imposé par le service.
        Role roleMembre = roleRepository.findByNom(ROLE_MEMBRE)
                .orElseThrow(() -> new RoleNotFoundException(ROLE_MEMBRE));

        Utilisateur membre = new Utilisateur();
        membre.setNom(dto.nom());
        membre.setPrenom(dto.prenom());
        membre.setTelephone(dto.telephone());
        // Mot de passe initial = numéro de téléphone (jamais choisi par le président), même règle que create().
        membre.setMotDePasse(passwordEncoder.encode(dto.telephone()));
        membre.setRoles(new HashSet<>(Set.of(roleMembre))); // un seul rôle : MEMBRE
        membre.setVillage(village); // le village déjà vérifié et chargé ci-dessus
        Utilisateur saved = utilisateurRepository.save(membre);
        log.info("Membre créé : id={}, village={}", saved.getId(), village.getNom());
        return UtilisateurMapper.toResponseDto(saved);
    }

    @Override
    public List<UtilisateurResponseDTO> findMembresDuVIllage(Long villageId, Utilisateur presidentConnecter) {

        // Même vérification que createMembre() : lève une exception si le village
        //n'existe pas,
        // ou si presidentConnecte ne le gère pas. On n'a pas besoin de l'entité
       // Village retournée ici
        // (contrairement à createMembre(), pas d'assignation à faire), juste de la
       // vérification elle-même.
        villageAccessService.verifierEtRecupererVillageGere(villageId,presidentConnecter);

        return utilisateurRepository.findByVillageId(villageId)
                                    .stream()
                                    .map(UtilisateurMapper::toResponseDto)
                                    .toList();
    }

    @Override
    public void delete(Long id) {
        if (!utilisateurRepository.existsById(id)) { // vérifie juste l'existence, sans charger l'entité
            throw new UtilisateurNotFoundException(id); // rien à supprimer
        }
        utilisateurRepository.deleteById(id); // DELETE en base
    }

    // Vérifie que le téléphone n'est pas déjà pris par UN AUTRE utilisateur.
    // excludedId = l'id de l'utilisateur qu'on est en train de modifier (null à la création).
    // Sans cette exclusion, modifier un utilisateur SANS changer son propre téléphone serait
    // rejeté à tort : son téléphone "existe déjà" en base... en la personne de lui-même.
    private void checkTelephoneDisponible(String telephone, Long excludedId) {
        utilisateurRepository.findByTelephone(telephone) // cherche si un utilisateur a déjà ce téléphone
                .ifPresent(existant -> { // ce bloc ne s'exécute QUE si un utilisateur a été trouvé
                    if (!existant.getId().equals(excludedId)) { // si ce n'est pas "soi-même" en modification
                        throw new TelephoneDejaUtiliseException(telephone); // alors le téléphone est vraiment pris ailleurs
                    }
                });
    }

    // Transforme un ensemble d'IDs de rôles en un ensemble d'entités Role réellement chargées depuis la base.
    // findAllById() fait UNE seule requête SQL pour tous les rôles demandés (au lieu d'une boucle de findById()).
    private Set<Role> resolveRoles(Set<Long> roleIds) {
        if (roleIds == null) { // le client peut ne pas envoyer de roleIds du tout
            return new HashSet<>(); // dans ce cas, aucun rôle attribué (ensemble vide, jamais null)
        }
        return new HashSet<>(roleRepository.findAllById(roleIds)); // charge les Role correspondants en une requête
    }

    // Transforme un id de village optionnel en entité Village réellement chargée depuis la base.
    // Contrairement à resolveRoles/resolveVillages (des ensembles), il n'y a ici qu'UN SEUL village au plus
    // (cohérent avec @ManyToOne sur l'entité) — d'où orElseThrow plutôt qu'un simple findAllById.
    private Village resolveVillage(Long villageId) {
        if (villageId == null) { // le client peut ne pas envoyer de villageId (cas ADMIN/PRESIDENT)
            return null; // "village" reste vide sur l'entité, exactement comme le veut le modèle
        }
        return villageRepository.findById(villageId)
                .orElseThrow(() -> new VillageNotFoundException(villageId)); // id fourni mais invalide -> 404 clair
    }

    // Transforme un ensemble d'IDs de villages en un ensemble d'entités Village réellement chargées.
    // Même logique que resolveRoles : findAllById() fait UNE seule requête SQL pour tous les villages demandés.
    private Set<Village> resolveVillages(Set<Long> villageIds) {
        if (villageIds == null) {
            return new HashSet<>();
        }
        return new HashSet<>(villageRepository.findAllById(villageIds));
    }
}
