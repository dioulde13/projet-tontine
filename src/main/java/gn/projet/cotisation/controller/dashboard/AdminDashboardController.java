package gn.projet.cotisation.controller.dashboard;

import gn.projet.cotisation.dto.dashboard.AdminDashboardResponseDTO;
import gn.projet.cotisation.service.dashboard.DashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Tableau de bord global (tous villages confondus), destiné à l'ADMIN.
@RestController
@RequestMapping("/api/admin/dashboard")
public class AdminDashboardController {

    private final DashboardService dashboardService;

    public AdminDashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    // GET /api/admin/dashboard
    @GetMapping
    public AdminDashboardResponseDTO get() {
        return dashboardService.getAdminDashboard();
    }
}
