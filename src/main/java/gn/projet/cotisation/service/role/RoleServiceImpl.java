package gn.projet.cotisation.service.role;

import gn.projet.cotisation.dto.mapper.RoleMapper; // conversion Entité <-> DTO
import gn.projet.cotisation.dto.roles.RoleResponseDTO; // DTO de sortie
import gn.projet.cotisation.dto.roles.UpdateRoleRequestDTO; // DTO d'entrée
import gn.projet.cotisation.entity.Role; // entité JPA
import gn.projet.cotisation.exception.role.RoleNotFoundException; // exception levée si id absent
import gn.projet.cotisation.repository.role.RoleRepository; // accès base de données
import org.springframework.stereotype.Service; // annotation qui déclare cette classe comme bean Spring

import java.util.List;

// @Service = spécialisation de @Component : dit à Spring de créer une instance de cette classe
// au démarrage et de la rendre injectable ailleurs (ici, dans RoleController).
@Service
public class RoleServiceImpl implements RoleService { // implémente le contrat défini par RoleService

    // Seul le service parle au repository ; le contrôleur, lui, ne connaîtra plus que le service
    private final RoleRepository roleRepository; // "final" : assigné une seule fois, dans le constructeur

    // Constructeur utilisé par Spring pour injecter automatiquement une instance de RoleRepository
    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository; // on garde la référence reçue dans le champ de la classe
    }

    @Override // indique qu'on implémente bien une méthode définie dans l'interface RoleService
    public List<RoleResponseDTO> findAll() {
        return roleRepository.findAll().stream() // récupère toutes les entités Role, puis ouvre un flux dessus
                .map(RoleMapper::toResponseDto) // convertit chaque Role en RoleResponseDTO
                .toList(); // rassemble le résultat du flux dans une List
    }

    @Override
    public RoleResponseDTO findById(Long id) {
        // orElseThrow(...) : si findById() renvoie un Optional vide, on lève l'exception au lieu
        // de continuer — impossible d'avoir un "role" null après cette ligne.
        Role role = roleRepository.findById(id) // cherche l'entité Role par id, renvoie Optional<Role>
                .orElseThrow(() -> new RoleNotFoundException(id)); // si absent, on arrête tout ici
        return RoleMapper.toResponseDto(role); // sinon, on convertit l'entité trouvée en DTO et on la renvoie
    }

    @Override
    public RoleResponseDTO create(UpdateRoleRequestDTO dto) {
        Role role = RoleMapper.toEntity(dto); // transforme le DTO reçu en entité Role (id encore null)
        Role saved = roleRepository.save(role); // INSERT en base ; "saved" a maintenant son id généré
        return RoleMapper.toResponseDto(saved); // on renvoie le DTO de réponse construit à partir de "saved"
    }

    @Override
    public RoleResponseDTO update(Long id, UpdateRoleRequestDTO dto) {
        Role role = roleRepository.findById(id) // cherche le rôle existant à modifier
                .orElseThrow(() -> new RoleNotFoundException(id)); // stoppe ici si l'id n'existe pas
        role.setNom(dto.nom()); // applique la nouvelle valeur envoyée par le client sur l'entité chargée
        return RoleMapper.toResponseDto(roleRepository.save(role)); // UPDATE en base, puis DTO de réponse
    }

    @Override
    public void delete(Long id) {
        if (!roleRepository.existsById(id)) { // vérifie l'existence sans charger l'entité entière (plus léger)
            throw new RoleNotFoundException(id); // rien à supprimer : on signale l'absence
        }
        roleRepository.deleteById(id); // DELETE en base
    }
}
