package gn.projet.cotisation.dto.dashboard;

import gn.projet.cotisation.dto.reunions.ReunionResponseDTO;
import gn.projet.cotisation.dto.villages.VillageResponseDTO;

import java.math.BigDecimal;

// Vue d'ensemble personnelle du MEMBRE connecté, scopée à lui-même et à son village.
public record MembreDashboardResponseDTO(
        VillageResponseDTO village,
        int moisCourant,
        int anneeCourante,
        // null si le village n'a pas encore de configuration
        BigDecimal montantCotisationMensuelle,
        boolean aPayeMoisCourant,
        BigDecimal montantTotalPaye,
        long nombrePaiements,
        long nombrePenalitesNonPayees,
        BigDecimal montantTotalPenalitesNonPayees,
        // null si aucune réunion n'est planifiée dans le futur pour son village
        ReunionResponseDTO prochaineReunion,
        long nombrePresences,
        long nombreAbsences,
        // pourcentage (0 à 100) de présences sur l'historique des réunions où ce membre était attendu
        double tauxPresence
) {
}
