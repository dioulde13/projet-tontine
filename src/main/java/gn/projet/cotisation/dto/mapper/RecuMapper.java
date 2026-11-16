package gn.projet.cotisation.dto.mapper;

import gn.projet.cotisation.dto.recus.RecuResponseDTO;
import gn.projet.cotisation.entity.Recu;

// Pas de toEntity() ici non plus (même raison que PaiementMapper) : construire un Recu a besoin
// de résoudre les Paiements via PaiementRepository à partir de paiementIds, donc ça reste dans le service.
public class RecuMapper {

    // Utilisé par la liste des reçus et la création : pas de nom résolu (évite une requête
    // supplémentaire par reçu quand on en affiche plusieurs d'un coup).
    public static RecuResponseDTO toResponseDto(Recu recu) {
        return toResponseDto(recu, null);
    }

    // Utilisé par le détail d'UN reçu (RecuServiceImpl.findOne), où creeParNom a déjà été résolu
    // par le service (seul endroit ayant accès à UtilisateurRepository).
    public static RecuResponseDTO toResponseDto(Recu recu, String creeParNom) {
        return new RecuResponseDTO(
                recu.getId(),
                recu.getNumero(),
                recu.getDateEmission(),
                recu.getPaiements().stream().map(PaiementMapper::toResponseDto).toList(),
                recu.getCreatedAt(),
                recu.getCreatedBy(),
                creeParNom
        );
    }
}
