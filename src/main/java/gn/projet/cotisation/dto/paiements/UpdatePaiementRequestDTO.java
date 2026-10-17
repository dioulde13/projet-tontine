package gn.projet.cotisation.dto.paiements;

import java.math.BigDecimal;
import java.time.LocalDate;

// DTO de REQUÊTE pour un paiement d'UN SEUL mois. L'opération "payer plusieurs mois d'un coup"
// (1 à 12 mois) sera une action de service à part, qui appellera cette logique une fois par mois —
// pas une structure de DTO différente.
public record UpdatePaiementRequestDTO(
        Long utilisateurId,
        Integer mois,
        Integer annee,
        BigDecimal montant,
        LocalDate datePaiement
) {
}
