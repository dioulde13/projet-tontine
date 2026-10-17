package gn.projet.cotisation.dto.penalites;

import gn.projet.cotisation.dto.utilisateurs.UtilisateurResponseDTO;
import gn.projet.cotisation.entity.StatutPenalite;
import gn.projet.cotisation.entity.TypePenalite;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PenaliteResponseDTO(
        Long id,
        UtilisateurResponseDTO utilisateur,
        TypePenalite type,
        BigDecimal montant,
        LocalDate date,
        StatutPenalite statut,
        Long paiementOrigineId,  // rempli seulement si type = RETARD_PAIEMENT
        // Mois/année du paiement en retard à l'origine de la pénalité (rempli seulement si
        // type = RETARD_PAIEMENT) : évite au client de devoir recharger le Paiement séparément
        // juste pour afficher la période concernée.
        Integer periodeMois,
        Integer periodeAnnee,
        Long presenceOrigineId   // rempli seulement si type = ABSENCE_REUNION
) {
}
