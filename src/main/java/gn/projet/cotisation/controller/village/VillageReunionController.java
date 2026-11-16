package gn.projet.cotisation.controller.village;

import gn.projet.cotisation.dto.reunions.CreateReunionRequestDTO;
import gn.projet.cotisation.dto.reunions.ReunionResponseDTO;
import gn.projet.cotisation.security.CustomUserDetails;
import gn.projet.cotisation.service.reunion.ReunionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/villages/{villageId}/reunions")
public class VillageReunionController {

    private final ReunionService reunionService;

    public VillageReunionController(ReunionService reunionService){
        this.reunionService = reunionService;
    }

    // POST /api/villages/{villageId}/reunions -> sans @PostMapping, cette méthode n'était ni routée
    // ni visible dans Swagger : le @RequestMapping de la classe fixe juste un préfixe commun,
    // chaque méthode a besoin de sa propre annotation pour devenir un vrai point d'entrée HTTP.
    @PostMapping
    public ReunionResponseDTO create(
            @PathVariable Long villageId,
            @Valid @RequestBody CreateReunionRequestDTO dto,
            @AuthenticationPrincipal CustomUserDetails userDetails
            ){
        return  reunionService.create(villageId,dto,userDetails.getUtilisateur());
    }

    @GetMapping
    public List<ReunionResponseDTO> list(
            @PathVariable Long villageId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ){
        return  reunionService.findReunionDuVillage(villageId, userDetails.getUtilisateur());
    }

    @DeleteMapping("/{reunionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable Long villageId,
            @PathVariable Long reunionId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        reunionService.delete(villageId, reunionId, userDetails.getUtilisateur());
    }

}
