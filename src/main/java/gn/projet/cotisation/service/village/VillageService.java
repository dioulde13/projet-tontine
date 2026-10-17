package gn.projet.cotisation.service.village;

import gn.projet.cotisation.dto.villages.UpdateVillageRequestDTO;
import gn.projet.cotisation.dto.villages.VillageResponseDTO;

import java.util.List;

public interface VillageService {

    List<VillageResponseDTO> findAll();

    VillageResponseDTO findById(Long id); // lève VillageNotFoundException si absent

    VillageResponseDTO create(UpdateVillageRequestDTO dto);

    VillageResponseDTO update(Long id, UpdateVillageRequestDTO dto); // lève VillageNotFoundException si absent

    void delete(Long id); // lève VillageNotFoundException si absent
}
