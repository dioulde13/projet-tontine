package gn.projet.cotisation.dto.mapper;

import gn.projet.cotisation.dto.paiements.PaiementResponseDTO;
import gn.projet.cotisation.entity.Paiement;

// Pas de toEntity() : construire un Paiement a besoin de résoudre l'Utilisateur (via UtilisateurRepository)
// à partir de utilisateurId, donc cette logique reste dans le service.
public class PaiementMapper {

    public static PaiementResponseDTO toResponseDto(Paiement paiement) {
        return new PaiementResponseDTO(
                paiement.getId(),
                UtilisateurMapper.toResponseDto(paiement.getUtilisateur()),
                paiement.getMois(),
                paiement.getAnnee(),
                paiement.getMontant(),
                paiement.getDatePaiement()
        );
    }
}
