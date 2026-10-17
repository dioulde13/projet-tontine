package gn.projet.cotisation.service.reunion;

import gn.projet.cotisation.dto.mapper.ReunionMapper;
import gn.projet.cotisation.dto.reunions.CreateReunionRequestDTO;
import gn.projet.cotisation.dto.reunions.ReunionResponseDTO;
import gn.projet.cotisation.entity.Reunion;
import gn.projet.cotisation.entity.Utilisateur;
import gn.projet.cotisation.entity.Village;
import gn.projet.cotisation.exception.reunion.ReunionNotFoundException;
import gn.projet.cotisation.repository.reunion.ReunionRepository;
import gn.projet.cotisation.service.village.VillageAccessService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReunionServiceImpl implements ReunionService {

    private final ReunionRepository reunionRepository;

    private final VillageAccessService villageAccessService;

    public ReunionServiceImpl(
            ReunionRepository reunionRepository,
            VillageAccessService villageAccessService
    ) {
        this.reunionRepository = reunionRepository;
        this.villageAccessService = villageAccessService;
    }

    @Override
    public ReunionResponseDTO create(
            Long villageId,
            CreateReunionRequestDTO dto,
            Utilisateur presidentConnecte
    ) {

       Village village =
               villageAccessService.verifierEtRecupererVillageGere(villageId, presidentConnecte);

       Reunion reunion = new Reunion();

       reunion.setVillage(village);
       reunion.setDateHeure(dto.dateHeure());
       reunion.setLieu(dto.lieu());

       Reunion saved = reunionRepository.save(reunion);
       return ReunionMapper.toResponseDto(saved);

    }

    @Override
    public List<ReunionResponseDTO> findReunionDuVillage(Long villageId, Utilisateur presidentConnecte) {

        villageAccessService.verifierEtRecupererVillageGere(villageId, presidentConnecte);

      return  reunionRepository.findByVillageId(villageId)
              .stream()
              .map(ReunionMapper::toResponseDto)
              .toList();
    }

    @Override
    public void delete(Long villageId, Long reunionId, Utilisateur presidentConnecte) {
        villageAccessService.verifierEtRecupererVillageGere(villageId, presidentConnecte);

        Reunion reunion = reunionRepository.findById(reunionId)
                .filter(r -> r.getVillage().getId().equals(villageId))
                .orElseThrow(() -> new ReunionNotFoundException(reunionId));

        reunionRepository.delete(reunion);
    }
}
