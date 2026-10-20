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
        // Toutes les pénalités (payées + non payées), contrairement à montantTotalPenalitesNonPayees ci-dessus.
        BigDecimal montantTotalPenalites,
        // Argent réellement collecté par le village : montantTotalCollecte (paiements) + les pénalités
        // déjà payées (elles aussi encaissées). Ne compte jamais les pénalités non payées.
        BigDecimal montantCaisse,
        long nombreReunions,
        // null si aucune réunion n'est planifiée dans le futur
        ReunionResponseDTO prochaineReunion,
        // pourcentage (0 à 100) de présences sur l'ensemble des réunions déjà tenues dans ce village
        double tauxPresence
) {
}
