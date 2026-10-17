package gn.projet.cotisation.controller.village;

import gn.projet.cotisation.dto.dashboard.PresidentDashboardResponseDTO;
import gn.projet.cotisation.security.CustomUserDetails;
import gn.projet.cotisation.service.dashboard.DashboardService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Tableau de bord d'UN village précis, destiné au PRESIDENT qui le gère.
@RestController
@RequestMapping("/api/villages/{villageId}/dashboard")
public class VillageDashboardController {

    private final DashboardService dashboardService;

    public VillageDashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    // GET /api/villages/{villageId}/dashboard
    @GetMapping
    public PresidentDashboardResponseDTO get(
            @PathVariable Long villageId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return dashboardService.getPresidentDashboard(villageId, userDetails.getUtilisateur());
    }
}
