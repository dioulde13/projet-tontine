package gn.projet.cotisation.dto.dashboard;

import gn.projet.cotisation.dto.reunions.ReunionResponseDTO;
import gn.projet.cotisation.dto.villages.VillageResponseDTO;

import java.math.BigDecimal;

// Vue d'ensemble d'UN village géré par le PRESIDENT connecté.
public record PresidentDashboardResponseDTO(
        VillageResponseDTO village,
        int moisCourant,
        int anneeCourante,
        long nombreMembres,
        // null si le président n'a pas encore configuré ce village (ConfigurationVillage absente)
        BigDecimal montantCotisationMensuelle,
        long nombreMembresAJourMoisCourant,
        long nombreMembresEnRetardMoisCourant,
        BigDecimal montantTotalCollecte,
        BigDecimal montantCollecteMoisCourant,
        long nombrePenalitesNonPayees,
        BigDecimal montantTotalPenalitesNonPayees,
        long nombreReunions,
        // null si aucune réunion n'est planifiée dans le futur
        ReunionResponseDTO prochaineReunion,
        // pourcentage (0 à 100) de présences sur l'ensemble des réunions déjà tenues dans ce village
        double tauxPresence
) {
}
