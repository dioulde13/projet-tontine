package gn.projet.cotisation.controller.dashboard;

import gn.projet.cotisation.dto.dashboard.MembreDashboardResponseDTO;
import gn.projet.cotisation.security.CustomUserDetails;
import gn.projet.cotisation.service.dashboard.DashboardService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Tableau de bord personnel du MEMBRE connecté (même principe que /api/utilisateurs/moi/villages :
// "moi" désigne l'utilisateur du token JWT, jamais un id dans l'URL).
@RestController
@RequestMapping("/api/membres/moi/dashboard")
public class MembreDashboardController {

    private final DashboardService dashboardService;

    public MembreDashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    // GET /api/membres/moi/dashboard
    @GetMapping
    public MembreDashboardResponseDTO get(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return dashboardService.getMembreDashboard(userDetails.getUtilisateur());
    }
}
