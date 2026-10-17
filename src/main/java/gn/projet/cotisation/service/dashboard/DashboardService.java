package gn.projet.cotisation.service.dashboard;

import gn.projet.cotisation.dto.dashboard.AdminDashboardResponseDTO;
import gn.projet.cotisation.dto.dashboard.MembreDashboardResponseDTO;
import gn.projet.cotisation.dto.dashboard.PresidentDashboardResponseDTO;
import gn.projet.cotisation.entity.Utilisateur;

public interface DashboardService {

    // Vue globale (tous villages confondus) : réservée à l'ADMIN, pas de scoping par village.
    AdminDashboardResponseDTO getAdminDashboard();

    // Vue d'UN village précis : presidentConnecte doit le gérer (même garde que les autres
    // fonctionnalités scopées par village, via VillageAccessService).
    PresidentDashboardResponseDTO getPresidentDashboard(Long villageId, Utilisateur presidentConnecte);

    // Vue personnelle du membre connecté, déduite de son propre village (pas de villageId dans l'URL).
    MembreDashboardResponseDTO getMembreDashboard(Utilisateur membreConnecte);
}
