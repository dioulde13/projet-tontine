package gn.projet.cotisation.controller.membre;

import gn.projet.cotisation.dto.membre.MembreCotisationsResponseDTO;
import gn.projet.cotisation.security.CustomUserDetails;
import gn.projet.cotisation.service.membre.MembreCotisationService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// Vue personnelle "mes cotisations" du MEMBRE connecté (même principe que /api/membres/moi/dashboard :
// "moi" désigne l'utilisateur du token JWT, jamais un id dans l'URL).
@RestController
@RequestMapping("/api/membres/moi/cotisations")
public class MembreCotisationController {

    private final MembreCotisationService membreCotisationService;

    public MembreCotisationController(MembreCotisationService membreCotisationService) {
        this.membreCotisationService = membreCotisationService;
    }

    // GET /api/membres/moi/cotisations?annee=2025 (annee optionnel, défaut = année courante)
    @GetMapping
    public MembreCotisationsResponseDTO get(
            @RequestParam(required = false) Integer annee,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return membreCotisationService.getMesCotisations(userDetails.getUtilisateur(), annee);
    }
}
