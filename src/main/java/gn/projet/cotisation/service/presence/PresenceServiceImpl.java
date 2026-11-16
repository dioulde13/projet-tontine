package gn.projet.cotisation.service.presence;

import gn.projet.cotisation.dto.mapper.PresenceMapper;
import gn.projet.cotisation.dto.presences.CreatePresenceRequestDTO;
import gn.projet.cotisation.dto.presences.PresenceResponseDTO;
import gn.projet.cotisation.entity.Presence;
import gn.projet.cotisation.entity.Reunion;
import gn.projet.cotisation.entity.Utilisateur;
import gn.projet.cotisation.exception.presence.PresenceNotFoundException;
import gn.projet.cotisation.exception.reunion.ReunionNotFoundException;
import gn.projet.cotisation.exception.utilisateur.UtilisateurNotFoundException;
import gn.projet.cotisation.repository.presence.PresenceRepository;
import gn.projet.cotisation.repository.reunion.ReunionRepository;
import gn.projet.cotisation.repository.utilisateur.UtilisateurRepository;
import gn.projet.cotisation.service.village.VillageAccessService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PresenceServiceImpl implements PresenceService{
    private  final PresenceRepository presenceRepository;
    private  final ReunionRepository reunionRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final VillageAccessService villageAccessService;

    public PresenceServiceImpl(
            PresenceRepository presenceRepository,
            ReunionRepository reunionRepository,
            UtilisateurRepository utilisateurRepository,
            VillageAccessService villageAccessService
    ){
        this.presenceRepository = presenceRepository;
        this.reunionRepository = reunionRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.villageAccessService = villageAccessService;
    }

    @Override
    public PresenceResponseDTO create(Long villageId, Long reunionId, CreatePresenceRequestDTO dto, Utilisateur presidentConnecte) {

        villageAccessService.verifierEtRecupererVillageGere(villageId, presidentConnecte);

        Reunion reunion = reunionRepository.findById(reunionId)
                .filter(r-> r.getVillage().getId().equals(villageId))
                .orElseThrow(() -> new ReunionNotFoundException(reunionId));

        Utilisateur membre = utilisateurRepository.findById(dto.utilisateurId())
                .filter(u -> u.getVillage() != null && u.getVillage().getId().equals(villageId))
                .orElseThrow(() -> new UtilisateurNotFoundException(dto.utilisateurId()));

        Presence presence = new Presence();
        presence.setReunion(reunion);
        presence.setUtilisateur(membre);
        presence.setPresent(dto.present());
        Presence saved = presenceRepository.save(presence);
        return PresenceMapper.toResponseDto(saved);
    }

    @Override
    public List<PresenceResponseDTO> findPresents(Long villageId, Long reunionId, Utilisateur presidentConnecte) {
         verifierEtRecupererReunion(villageId, reunionId, presidentConnecte);

        return presenceRepository.findPresentsByReunionId(reunionId)
                .stream()
                .map(PresenceMapper::toResponseDto)
                .toList();
    }
    @Override
    public List<PresenceResponseDTO> findAbsences(Long villageId, Long reunionId, Utilisateur presidentConnecte) {
        verifierEtRecupererReunion(villageId, reunionId, presidentConnecte);

        return presenceRepository.findAbsencesByReunionId(reunionId)
                .stream()
                .map(PresenceMapper::toResponseDto)
                .toList();
    }


    private Reunion verifierEtRecupererReunion(Long villageId, Long reunionId, Utilisateur presidentConnecte){
        villageAccessService.verifierEtRecupererVillageGere(villageId, presidentConnecte);
        return reunionRepository.findById(reunionId)
                .filter(r -> r.getVillage().getId().equals(villageId))
                .orElseThrow(() -> new ReunionNotFoundException(reunionId));
    }

    @Override
    public void delete(Long villageId, Long reunionId, Long presenceId, Utilisateur presidentConnecte) {
        verifierEtRecupererReunion(villageId, reunionId, presidentConnecte);

        Presence presence = presenceRepository.findById(presenceId)
                .filter(p -> p.getReunion().getId().equals(reunionId))
                .orElseThrow(() -> new PresenceNotFoundException(presenceId));

        presenceRepository.delete(presence);
    }
}
