package gn.projet.cotisation.service.depense;

import gn.projet.cotisation.dto.depenses.CreateDepenseRequestDTO;
import gn.projet.cotisation.dto.depenses.DepenseResponseDTO;
import gn.projet.cotisation.dto.mapper.DepenseMapper;
import gn.projet.cotisation.entity.Depense;
import gn.projet.cotisation.entity.TypeMouvementCaisse;
import gn.projet.cotisation.entity.Utilisateur;
import gn.projet.cotisation.entity.Village;
import gn.projet.cotisation.exception.depense.DepenseNotFoundException;
import gn.projet.cotisation.exception.typemouvementcaisse.TypeMouvementCaisseNotFoundException;
import gn.projet.cotisation.repository.depense.DepenseRepository;
import gn.projet.cotisation.repository.typemouvementcaisse.TypeMouvementCaisseRepository;
import gn.projet.cotisation.repository.utilisateur.UtilisateurRepository;
import gn.projet.cotisation.service.village.VillageAccessService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class DepenseServiceImpl implements DepenseService {

    private final DepenseRepository depenseRepository;
    private final TypeMouvementCaisseRepository typeMouvementCaisseRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final VillageAccessService villageAccessService;

    public DepenseServiceImpl(
            DepenseRepository depenseRepository,
            TypeMouvementCaisseRepository typeMouvementCaisseRepository,
            UtilisateurRepository utilisateurRepository,
            VillageAccessService villageAccessService
    ) {
        this.depenseRepository = depenseRepository;
        this.typeMouvementCaisseRepository = typeMouvementCaisseRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.villageAccessService = villageAccessService;
    }

    @Transactional
    @Override
    public DepenseResponseDTO create(Long villageId, CreateDepenseRequestDTO dto, Utilisateur presidentConnecte) {
        Village village = villageAccessService.verifierEtRecupererVillageGere(villageId, presidentConnecte);

        TypeMouvementCaisse type = typeMouvementCaisseRepository.findById(dto.typeId())
                .filter(t -> t.getVillage().getId().equals(villageId))
                .orElseThrow(() -> new TypeMouvementCaisseNotFoundException(dto.typeId()));

        Depense depense = new Depense();
        depense.setVillage(village);
        depense.setType(type);
        depense.setMontant(dto.montant());
        depense.setMotif(dto.motif());
        depense.setDate(dto.date());
        depense.setDateEmissionRecu(LocalDate.now());
        // numeroRecu est NOT NULL + UNIQUE mais dépend de l'id, inconnu avant le premier INSERT
        // (GenerationType.IDENTITY). On pose un placeholder unique le temps de cet INSERT, puis on le
        // remplace par le vrai numéro basé sur l'id généré, via un second save (UPDATE) dans la même
        // transaction — pas besoin d'un compteur séparé ni d'une table Recu dédiée, une dépense
        // correspondant toujours à exactement un reçu.
        depense.setNumeroRecu("TEMP-" + UUID.randomUUID());

        Depense saved = depenseRepository.save(depense);
        saved.setNumeroRecu("DEP-" + LocalDate.now().getYear() + "-" + String.format("%05d", saved.getId()));
        Depense avecRecu = depenseRepository.save(saved);

        return DepenseMapper.toResponseDto(avecRecu);
    }

    @Override
    public List<DepenseResponseDTO> findAllByVillage(Long villageId, Utilisateur presidentConnecte) {
        villageAccessService.verifierEtRecupererVillageGere(villageId, presidentConnecte);
        return depenseRepository.findByVillageId(villageId).stream()
                .map(DepenseMapper::toResponseDto)
                .toList();
    }

    @Override
    public DepenseResponseDTO findOne(Long villageId, Long depenseId, Utilisateur presidentConnecte) {
        villageAccessService.verifierEtRecupererVillageGere(villageId, presidentConnecte);

        Depense depense = depenseRepository.findById(depenseId)
                .filter(d -> d.getVillage().getId().equals(villageId))
                .orElseThrow(() -> new DepenseNotFoundException(depenseId));

        return DepenseMapper.toResponseDto(depense, resoudreNomComplet(depense.getCreatedBy()));
    }

    @Override
    public void delete(Long villageId, Long depenseId, Utilisateur presidentConnecte) {
        villageAccessService.verifierEtRecupererVillageGere(villageId, presidentConnecte);

        Depense depense = depenseRepository.findById(depenseId)
                .filter(d -> d.getVillage().getId().equals(villageId))
                .orElseThrow(() -> new DepenseNotFoundException(depenseId));

        depenseRepository.delete(depense);
    }

    // Même logique que RecuServiceImpl.resoudreNomComplet : affichage uniquement, jamais d'exception
    // si le téléphone est null ou ne correspond plus à personne (ex: compte supprimé depuis).
    private String resoudreNomComplet(String telephone) {
        if (telephone == null) {
            return null;
        }
        return utilisateurRepository.findByTelephone(telephone)
                .map(u -> u.getPrenom() + " " + u.getNom())
                .orElse(null);
    }
}
