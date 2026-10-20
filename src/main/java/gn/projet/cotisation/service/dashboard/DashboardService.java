package gn.projet.cotisation.service.dashboard;

import gn.projet.cotisation.dto.dashboard.AdminDashboardResponseDTO;
import gn.projet.cotisation.dto.dashboard.MembreDashboardResponseDTO;
import gn.projet.cotisation.dto.dashboard.PresidentDashboardResponseDTO;
import gn.projet.cotisation.entity.Utilisateur;

import java.time.LocalDate;

public interface DashboardService {

    // Vue globale (tous villages confondus) : réservée à l'ADMIN, pas de scoping par village.
    AdminDashboardResponseDTO getAdminDashboard();

    // Vue d'UN village précis : presidentConnecte doit le gérer (même garde que les autres
    // fonctionnalités scopées par village, via VillageAccessService). dateDebut/dateFin (bornes
    // incluses, toutes deux optionnelles) restreignent les agrégats historiques (collecté, pénalités,
    // réunions, taux de présence) à cette période ; null = aucune borne (comportement par défaut,
    // "depuis toujours"). Les indicateurs liés au mois en cours (membres à jour, cotisation
    // mensuelle...) restent eux toujours basés sur aujourd'hui, filtre ou non.
    PresidentDashboardResponseDTO getPresidentDashboard(
            Long villageId, Utilisateur presidentConnecte, LocalDate dateDebut, LocalDate dateFin
    );

    // Vue personnelle du membre connecté, déduite de son propre village (pas de villageId dans l'URL).
    MembreDashboardResponseDTO getMembreDashboard(Utilisateur membreConnecte);
}
