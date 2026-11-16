package gn.projet.cotisation.service.configurationvillage;

import gn.projet.cotisation.dto.configurationvillages.ConfigurationVillageResponseDTO;
import gn.projet.cotisation.dto.configurationvillages.UpdateConfigurationVillageRequestDTO;
import gn.projet.cotisation.dto.mapper.ConfigurationVillageMapper;
import gn.projet.cotisation.entity.ConfigurationVillage;
import gn.projet.cotisation.entity.Utilisateur;
import gn.projet.cotisation.entity.Village;
import gn.projet.cotisation.exception.configurationvillage.ConfigurationVillageNotFoundException;
import gn.projet.cotisation.repository.configurationVillage.ConfigurationVillageRepository;
import gn.projet.cotisation.repository.utilisateur.UtilisateurRepository;
import gn.projet.cotisation.service.village.VillageAccessService;
import org.springframework.stereotype.Service;

@Service
public class ConfigurationVillageServiceImpl implements ConfigurationVillageService{

    private final ConfigurationVillageRepository configurationVillageRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final VillageAccessService villageAccessService;

    public ConfigurationVillageServiceImpl(
            ConfigurationVillageRepository configurationVillageRepository,
            UtilisateurRepository utilisateurRepository,
            VillageAccessService villageAccessService
    ) {
        this.configurationVillageRepository = configurationVillageRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.villageAccessService = villageAccessService;
    }

    // Retrouve le nom complet (prénom + nom) de l'utilisateur portant ce téléphone, pour affichage
    // uniquement. Null si telephone est null ou si plus personne ne le porte (ex: compte supprimé).
    private String resoudreNomComplet(String telephone) {
        if (telephone == null) {
            return null;
        }
        return utilisateurRepository.findByTelephone(telephone)
                .map(u -> u.getPrenom() + " " + u.getNom())
                .orElse(null);
    }

    @Override
    public ConfigurationVillageResponseDTO create(Long villageId, UpdateConfigurationVillageRequestDTO dto, Utilisateur presidentConnecte) {
        Village village = villageAccessService.verifierEtRecupererVillageGere(villageId, presidentConnecte);
        ConfigurationVillage config = new ConfigurationVillage();
        config.setVillage(village);
        config.setMontantCotisationMensuelle(dto.montantCotisationMensuelle());
        config.setMontantPenaliteRetard(dto.montantPenaliteRetard());
        config.setMontantPenaliteAbsenceReunion(dto.montantPenaliteAbsenceReunion());

        ConfigurationVillage saved = configurationVillageRepository.save(config);
        return ConfigurationVillageMapper.toResponseDto(saved, resoudreNomComplet(saved.getUpdatedBy()));
    }

    @Override
    public ConfigurationVillageResponseDTO findByVillage(Long villageId, Utilisateur presidentConnecte) {
        ConfigurationVillage config =
                configurationVillageRepository.findByVillageId(villageId)
                        .orElseThrow(() -> new
                                ConfigurationVillageNotFoundException(villageId));
        return ConfigurationVillageMapper.toResponseDto(config, resoudreNomComplet(config.getUpdatedBy()));
    }

    @Override
    public ConfigurationVillageResponseDTO update(Long villageId, UpdateConfigurationVillageRequestDTO dto, Utilisateur presidentConnecte) {
        villageAccessService.verifierEtRecupererVillageGere(villageId, presidentConnecte);

        ConfigurationVillage config = configurationVillageRepository.findByVillageId(villageId)
                .orElseThrow(() -> new ConfigurationVillageNotFoundException(villageId));

        config.setMontantCotisationMensuelle(dto.montantCotisationMensuelle());
        config.setMontantPenaliteRetard(dto.montantPenaliteRetard());
        config.setMontantPenaliteAbsenceReunion(dto.montantPenaliteAbsenceReunion());

        ConfigurationVillage saved = configurationVillageRepository.save(config);
        return ConfigurationVillageMapper.toResponseDto(saved, resoudreNomComplet(saved.getUpdatedBy()));
    }
}
