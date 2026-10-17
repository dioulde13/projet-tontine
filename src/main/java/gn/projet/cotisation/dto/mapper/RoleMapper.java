package gn.projet.cotisation.dto.mapper;

import gn.projet.cotisation.dto.roles.RoleResponseDTO;
import gn.projet.cotisation.dto.roles.UpdateRoleRequestDTO;
import gn.projet.cotisation.entity.Role;

// Classe utilitaire (méthodes statiques) qui convertit entre l'entité Role et ses DTO de requête/réponse.
public class RoleMapper {

    // Entité -> DTO de réponse : ce qu'on renvoie au client
    public static RoleResponseDTO toResponseDto(Role role) {
        return new RoleResponseDTO(role.getId(), role.getNom());
    }

    // DTO de requête -> Entité : utilisé quand le client envoie un UpdateRoleRequestDTO pour créer/modifier
    public static Role toEntity(UpdateRoleRequestDTO dto) {
        Role role = new Role();
        role.setNom(dto.nom());
        return role;
    }
}
