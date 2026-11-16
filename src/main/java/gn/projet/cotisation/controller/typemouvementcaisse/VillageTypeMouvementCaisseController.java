package gn.projet.cotisation.controller.typemouvementcaisse;

import gn.projet.cotisation.dto.typemouvementcaisse.TypeMouvementCaisseResponseDTO;
import gn.projet.cotisation.dto.typemouvementcaisse.UpdateTypeMouvementCaisseRequestDTO;
import gn.projet.cotisation.security.CustomUserDetails;
import gn.projet.cotisation.service.typemouvementcaisse.TypeMouvementCaisseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/villages/{villageId}/types-depenses")
public class VillageTypeMouvementCaisseController {

    private final TypeMouvementCaisseService typeMouvementCaisseService;

    public VillageTypeMouvementCaisseController(TypeMouvementCaisseService typeMouvementCaisseService) {
        this.typeMouvementCaisseService = typeMouvementCaisseService;
    }

    @GetMapping
    public List<TypeMouvementCaisseResponseDTO> list(
            @PathVariable Long villageId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return typeMouvementCaisseService.findAllByVillage(villageId, userDetails.getUtilisateur());
    }

    @PostMapping
    public TypeMouvementCaisseResponseDTO create(
            @PathVariable Long villageId,
            @Valid @RequestBody UpdateTypeMouvementCaisseRequestDTO dto,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return typeMouvementCaisseService.create(villageId, dto, userDetails.getUtilisateur());
    }

    @PutMapping("/{typeId}")
    public TypeMouvementCaisseResponseDTO update(
            @PathVariable Long villageId,
            @PathVariable Long typeId,
            @Valid @RequestBody UpdateTypeMouvementCaisseRequestDTO dto,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return typeMouvementCaisseService.update(villageId, typeId, dto, userDetails.getUtilisateur());
    }

    @DeleteMapping("/{typeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable Long villageId,
            @PathVariable Long typeId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        typeMouvementCaisseService.delete(villageId, typeId, userDetails.getUtilisateur());
    }
}
