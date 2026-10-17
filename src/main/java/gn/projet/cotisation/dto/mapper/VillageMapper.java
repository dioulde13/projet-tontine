package gn.projet.cotisation.dto.mapper;

import gn.projet.cotisation.dto.villages.UpdateVillageRequestDTO;
import gn.projet.cotisation.dto.villages.VillageResponseDTO;
import gn.projet.cotisation.entity.Village;

// Village n'a aucune relation à résoudre (pas de clé étrangère) : toEntity() peut donc vivre ici,
// comme pour RoleMapper — contrairement à UtilisateurMapper qui a besoin d'un repository pour les rôles.
public class VillageMapper {

    public static VillageResponseDTO toResponseDto(Village village) {
        return new VillageResponseDTO(village.getId(), village.getNom());
    }

    public static Village toEntity(UpdateVillageRequestDTO dto) {
        Village village = new Village();
        village.setNom(dto.nom());
        return village;
    }
}
