package gn.projet.cotisation.dto.dashboard;

import java.math.BigDecimal;

// Vue d'ensemble globale (tous villages confondus), réservée à l'ADMIN.
public record AdminDashboardResponseDTO(
        long nombreVillages,
        long nombreAdmins,
        long nombrePresidents,
        long nombreMembres,
        long nombreReunions,
        long nombrePaiements,
        BigDecimal montantTotalCotisationsCollectees,
        long nombrePenalitesNonPayees,
        BigDecimal montantTotalPenalitesNonPayees
) {
}
