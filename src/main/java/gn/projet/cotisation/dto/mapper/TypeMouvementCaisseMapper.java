package gn.projet.cotisation.dto.mapper;

import gn.projet.cotisation.dto.typemouvementcaisse.TypeMouvementCaisseResponseDTO;
import gn.projet.cotisation.entity.TypeMouvementCaisse;

public class TypeMouvementCaisseMapper {

    public static TypeMouvementCaisseResponseDTO toResponseDto(TypeMouvementCaisse type) {
        return new TypeMouvementCaisseResponseDTO(type.getId(), type.getLibelle());
    }
}
