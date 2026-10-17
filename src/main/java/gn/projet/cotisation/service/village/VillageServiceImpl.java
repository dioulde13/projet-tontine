package gn.projet.cotisation.service.village;

import gn.projet.cotisation.dto.mapper.VillageMapper;
import gn.projet.cotisation.dto.villages.UpdateVillageRequestDTO;
import gn.projet.cotisation.dto.villages.VillageResponseDTO;
import gn.projet.cotisation.entity.Village;
import gn.projet.cotisation.exception.village.VillageNotFoundException;
import gn.projet.cotisation.repository.village.VillageRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VillageServiceImpl implements VillageService {

    private final VillageRepository villageRepository;

    public VillageServiceImpl(VillageRepository villageRepository) {
        this.villageRepository = villageRepository;
    }

    @Override
    public List<VillageResponseDTO> findAll() {
        return villageRepository.findAll().stream()
                .map(VillageMapper::toResponseDto)
                .toList();
    }

    @Override
    public VillageResponseDTO findById(Long id) {
        Village village = villageRepository.findById(id)
                .orElseThrow(() -> new VillageNotFoundException(id));
        return VillageMapper.toResponseDto(village);
    }

    @Override
    public VillageResponseDTO create(UpdateVillageRequestDTO dto) {
        Village village = VillageMapper.toEntity(dto);
        Village saved = villageRepository.save(village);
        return VillageMapper.toResponseDto(saved);
    }

    @Override
    public VillageResponseDTO update(Long id, UpdateVillageRequestDTO dto) {
        Village village = villageRepository.findById(id)
                .orElseThrow(() -> new VillageNotFoundException(id));
        village.setNom(dto.nom());
        return VillageMapper.toResponseDto(villageRepository.save(village));
    }

    @Override
    public void delete(Long id) {
        if (!villageRepository.existsById(id)) {
            throw new VillageNotFoundException(id);
        }
        villageRepository.deleteById(id);
    }
}
