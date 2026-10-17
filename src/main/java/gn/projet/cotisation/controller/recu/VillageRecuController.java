package gn.projet.cotisation.controller.recu;

import gn.projet.cotisation.dto.recus.CreateRecuRequestDTO;
import gn.projet.cotisation.dto.recus.RecuResponseDTO;
import gn.projet.cotisation.security.CustomUserDetails;
import gn.projet.cotisation.service.recu.RecuService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/villages/{villageId}/recus")
public class VillageRecuController {

    private final RecuService recuService;

    public VillageRecuController(RecuService recuService) {
        this.recuService = recuService;
    }

    @PostMapping
    public RecuResponseDTO create(
            @PathVariable Long villageId,
            @RequestBody CreateRecuRequestDTO dto,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return recuService.create(villageId, dto, userDetails.getUtilisateur());
    }

    @GetMapping
    public List<RecuResponseDTO> list(
            @PathVariable Long villageId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return recuService.findRecusByVillage(villageId, userDetails.getUtilisateur());
    }

    @GetMapping("/{recuId}")
    public RecuResponseDTO findOne(
            @PathVariable Long villageId,
            @PathVariable Long recuId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return recuService.findOne(villageId, recuId, userDetails.getUtilisateur());
    }
}
