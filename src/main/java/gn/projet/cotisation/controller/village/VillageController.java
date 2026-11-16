package gn.projet.cotisation.controller.village;

import gn.projet.cotisation.dto.villages.UpdateVillageRequestDTO;
import gn.projet.cotisation.dto.villages.VillageResponseDTO;
import gn.projet.cotisation.service.village.VillageService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/villages")
public class VillageController {

    private final VillageService villageService;

    public VillageController(VillageService villageService) {
        this.villageService = villageService;
    }

    // GET /api/villages
    @GetMapping
    public List<VillageResponseDTO> getAll() {
        return villageService.findAll();
    }

    // GET /api/villages/{id}
    @GetMapping("/{id}")
    public VillageResponseDTO getById(@PathVariable Long id) {
        return villageService.findById(id);
    }

    // POST /api/villages
    @PostMapping
    public VillageResponseDTO create(@Valid @RequestBody UpdateVillageRequestDTO dto) {
        return villageService.create(dto);
    }

    // PUT /api/villages/{id}
    @PutMapping("/{id}")
    public VillageResponseDTO update(@PathVariable Long id, @Valid @RequestBody UpdateVillageRequestDTO dto) {
        return villageService.update(id, dto);
    }

    // DELETE /api/villages/{id}
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        villageService.delete(id);
    }
}
