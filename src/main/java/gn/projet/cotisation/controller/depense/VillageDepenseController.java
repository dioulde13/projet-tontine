package gn.projet.cotisation.controller.depense;

import gn.projet.cotisation.dto.depenses.CreateDepenseRequestDTO;
import gn.projet.cotisation.dto.depenses.DepenseResponseDTO;
import gn.projet.cotisation.security.CustomUserDetails;
import gn.projet.cotisation.service.depense.DepenseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/villages/{villageId}/depenses")
public class VillageDepenseController {

    private final DepenseService depenseService;

    public VillageDepenseController(DepenseService depenseService) {
        this.depenseService = depenseService;
    }

    @PostMapping
    public DepenseResponseDTO create(
            @PathVariable Long villageId,
            @Valid @RequestBody CreateDepenseRequestDTO dto,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return depenseService.create(villageId, dto, userDetails.getUtilisateur());
    }

    @GetMapping
    public List<DepenseResponseDTO> list(
            @PathVariable Long villageId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return depenseService.findAllByVillage(villageId, userDetails.getUtilisateur());
    }

    @GetMapping("/{depenseId}")
    public DepenseResponseDTO findOne(
            @PathVariable Long villageId,
            @PathVariable Long depenseId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return depenseService.findOne(villageId, depenseId, userDetails.getUtilisateur());
    }

    @DeleteMapping("/{depenseId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable Long villageId,
            @PathVariable Long depenseId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        depenseService.delete(villageId, depenseId, userDetails.getUtilisateur());
    }
}
