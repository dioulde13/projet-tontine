package gn.projet.cotisation.controller.penalite;

import gn.projet.cotisation.dto.penalites.PenaliteResponseDTO;
import gn.projet.cotisation.dto.penalites.UpdatePenaliteRequestDTO;
import gn.projet.cotisation.security.CustomUserDetails;
import gn.projet.cotisation.service.penalite.PenaliteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/villages/{villageId}/penalites")
public class VillagePenaliteController {

    private final PenaliteService penaliteService;

    public VillagePenaliteController(PenaliteService penaliteService) {
        this.penaliteService = penaliteService;
    }

    @PostMapping
    public PenaliteResponseDTO create(
            @PathVariable Long villageId,
            @Valid @RequestBody UpdatePenaliteRequestDTO dto,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return penaliteService.create(villageId, dto,
                userDetails.getUtilisateur());
    }

    @GetMapping
    public List<PenaliteResponseDTO> list(
            @PathVariable Long villageId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return penaliteService.findAllPenalitesVillage(villageId,
                userDetails.getUtilisateur());
    }

    @DeleteMapping("/{penaliteId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable Long villageId,
            @PathVariable Long penaliteId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        penaliteService.delete(villageId, penaliteId, userDetails.getUtilisateur());
    }

    @PostMapping("/{penaliteId}/payer")
    public PenaliteResponseDTO payer(
            @PathVariable Long villageId,
            @PathVariable Long penaliteId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return penaliteService.payer(villageId, penaliteId, userDetails.getUtilisateur());
    }

}
