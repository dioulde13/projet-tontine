package gn.projet.cotisation.dto.mapper;

import gn.projet.cotisation.dto.depenses.DepenseResponseDTO;
import gn.projet.cotisation.entity.Depense;

// Pas de toEntity() : construire une Depense a besoin de résoudre le TypeMouvementCaisse (via son
// repository) à partir de typeId, donc cette logique reste dans le service (même raison que PaiementMapper).
public class DepenseMapper {

    // Utilisé par la liste : pas de nom résolu (évite une requête supplémentaire par dépense).
    public static DepenseResponseDTO toResponseDto(Depense depense) {
        return toResponseDto(depense, null);
    }

    // Utilisé par le détail d'UNE dépense (DepenseServiceImpl.findOne), où creeParNom a déjà été
    // résolu par le service (seul endroit ayant accès à UtilisateurRepository).
    public static DepenseResponseDTO toResponseDto(Depense depense, String creeParNom) {
        return new DepenseResponseDTO(
                depense.getId(),
                TypeMouvementCaisseMapper.toResponseDto(depense.getType()),
                depense.getMontant(),
                depense.getMotif(),
                depense.getDate(),
                depense.getNumeroRecu(),
                depense.getDateEmissionRecu(),
                depense.getCreatedAt(),
                depense.getCreatedBy(),
                creeParNom
        );
    }
}
