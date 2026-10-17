package gn.projet.cotisation.dto.mapper;

import gn.projet.cotisation.dto.reunions.ReunionResponseDTO;
import gn.projet.cotisation.entity.Reunion;

// Pas de toEntity() : a besoin de résoudre le Village (via VillageRepository) à partir de villageId.
public class ReunionMapper {

    public static ReunionResponseDTO toResponseDto(Reunion reunion) {
        return new ReunionResponseDTO(
                reunion.getId(),
                VillageMapper.toResponseDto(reunion.getVillage()), // VillageMapper, pas ReunionMapper : on convertit un Village ici
                reunion.getDateHeure(),
                reunion.getLieu()
        );
    }
}
