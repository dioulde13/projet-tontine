package gn.projet.cotisation.controller.village;

import gn.projet.cotisation.dto.configurationvillages.ConfigurationVillageResponseDTO;
import gn.projet.cotisation.dto.configurationvillages.UpdateConfigurationVillageRequestDTO;
import gn.projet.cotisation.security.CustomUserDetails;
import gn.projet.cotisation.service.configurationvillage.ConfigurationVillageService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/villages/{villageId}/configuration")
public class VillageConfigurationController {

    private final ConfigurationVillageService configurationVillageService;

    public VillageConfigurationController(ConfigurationVillageService
                                                  configurationVillageService) {
        this.configurationVillageService = configurationVillageService;
    }
    @PostMapping
    public ConfigurationVillageResponseDTO create(
            @PathVariable Long villageId,
            @Valid @RequestBody UpdateConfigurationVillageRequestDTO dto,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return configurationVillageService.create(villageId, dto,
                userDetails.getUtilisateur());
    }
    @GetMapping
    public ConfigurationVillageResponseDTO get(
            @PathVariable Long villageId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return configurationVillageService.findByVillage(villageId,
                userDetails.getUtilisateur());
    }

    @PutMapping
    public ConfigurationVillageResponseDTO update(
            @PathVariable Long villageId,
            @Valid @RequestBody UpdateConfigurationVillageRequestDTO dto,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return configurationVillageService.update(villageId, dto,
                userDetails.getUtilisateur());
    }

}
