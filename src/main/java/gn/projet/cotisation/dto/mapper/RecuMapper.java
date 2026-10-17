package gn.projet.cotisation.dto.mapper;

import gn.projet.cotisation.dto.recus.RecuResponseDTO;
import gn.projet.cotisation.entity.Recu;

// Pas de toEntity() ici non plus (même raison que PaiementMapper) : construire un Recu a besoin
// de résoudre les Paiements via PaiementRepository à partir de paiementIds, donc ça reste dans le service.
public class RecuMapper {

    public static RecuResponseDTO toResponseDto(Recu recu) {
        return new RecuResponseDTO(
                recu.getId(),
                recu.getNumero(),
                recu.getDateEmission(),
                recu.getPaiements().stream().map(PaiementMapper::toResponseDto).toList()
        );
    }
}
