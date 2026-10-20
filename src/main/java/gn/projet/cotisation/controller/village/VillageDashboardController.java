package gn.projet.cotisation.controller.village;

import gn.projet.cotisation.dto.dashboard.PresidentDashboardResponseDTO;
import gn.projet.cotisation.security.CustomUserDetails;
import gn.projet.cotisation.service.dashboard.DashboardService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

// Tableau de bord d'UN village précis, destiné au PRESIDENT qui le gère.
@RestController
@RequestMapping("/api/villages/{villageId}/dashboard")
public class VillageDashboardController {

    private final DashboardService dashboardService;

    public VillageDashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    // GET /api/villages/{villageId}/dashboard?dateDebut=2026-01-01&dateFin=2026-01-31
    // dateDebut/dateFin optionnelles (format ISO yyyy-MM-dd) : restreignent les agrégats historiques
    // du tableau de bord à cette période. Voir DashboardService.getPresidentDashboard pour le détail.
    @GetMapping
    public PresidentDashboardResponseDTO get(
            @PathVariable Long villageId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return dashboardService.getPresidentDashboard(villageId, userDetails.getUtilisateur(), dateDebut, dateFin);
    }
}
