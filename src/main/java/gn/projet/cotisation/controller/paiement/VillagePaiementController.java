package gn.projet.cotisation.controller.paiement;

import gn.projet.cotisation.dto.paiements.CreatePaiementRequestDTO;
import gn.projet.cotisation.dto.paiements.PaiementResponseDTO;
import gn.projet.cotisation.security.CustomUserDetails;
import gn.projet.cotisation.service.paiement.PaiementServices;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/villages/{villageId}/paiements")
public class VillagePaiementController {

    private final PaiementServices paiementServices;

    public VillagePaiementController(PaiementServices paiementServices) {
        this.paiementServices = paiementServices;
    }

    @PostMapping
    public List<PaiementResponseDTO> create(
            @PathVariable Long villageId,
            @RequestBody CreatePaiementRequestDTO dto,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return paiementServices.create(villageId, dto, userDetails.getUtilisateur());
    }

    @GetMapping
    public List<PaiementResponseDTO> list(
            @PathVariable Long villageId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return paiementServices.findPaiementByVillage(villageId, userDetails.getUtilisateur());
    }

    @DeleteMapping("/{paiementId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable Long villageId,
            @PathVariable Long paiementId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        paiementServices.delete(villageId, paiementId, userDetails.getUtilisateur());
    }
}
