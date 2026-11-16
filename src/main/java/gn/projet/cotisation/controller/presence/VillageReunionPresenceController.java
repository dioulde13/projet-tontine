package gn.projet.cotisation.controller.presence;

import gn.projet.cotisation.dto.presences.CreatePresenceRequestDTO;
import gn.projet.cotisation.dto.presences.PresenceResponseDTO;
import gn.projet.cotisation.entity.Utilisateur;
import gn.projet.cotisation.security.CustomUserDetails;
import gn.projet.cotisation.service.presence.PresenceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// @RestController (pas @Controller) : sans ça, Spring MVC essaie d'interpréter la valeur renvoyée
// par create() comme le NOM D'UNE VUE HTML à afficher, au lieu de la convertir en JSON — d'où le 500.
// @RestController = @Controller + @ResponseBody appliqué automatiquement à toutes les méthodes.
@RestController
@RequestMapping("/api/villages/{villageId}/reunion/{reunionId}/presences")
public class VillageReunionPresenceController {
    private final PresenceService presenceService;

    public  VillageReunionPresenceController(
        PresenceService presenceService
    ){
       this.presenceService = presenceService;
    }

    @PostMapping
    public PresenceResponseDTO create(
            @PathVariable Long villageId,
            @PathVariable Long reunionId,
            @Valid @RequestBody CreatePresenceRequestDTO dto,
            @AuthenticationPrincipal CustomUserDetails userDetails
            ){
        return presenceService.create(villageId, reunionId, dto, userDetails.getUtilisateur());
    }

    @GetMapping("/present")
    public List<PresenceResponseDTO> listPresent(
            @PathVariable Long villageId,
            @PathVariable Long reunionId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ){
        return presenceService.findPresents(villageId, reunionId, userDetails.getUtilisateur());
    }

    @GetMapping("/Absent")
    public List<PresenceResponseDTO> listAbsence(
            @PathVariable Long villageId,
            @PathVariable Long reunionId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ){
        return presenceService.findAbsences(villageId, reunionId, userDetails.getUtilisateur());
    }

    @DeleteMapping("/{presenceId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable Long villageId,
            @PathVariable Long reunionId,
            @PathVariable Long presenceId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        presenceService.delete(villageId, reunionId, presenceId, userDetails.getUtilisateur());
    }

}
