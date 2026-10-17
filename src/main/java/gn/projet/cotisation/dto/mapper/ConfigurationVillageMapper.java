package gn.projet.cotisation.dto.mapper;

import gn.projet.cotisation.dto.configurationvillages.ConfigurationVillageResponseDTO;
import gn.projet.cotisation.entity.ConfigurationVillage;

// Pas de toEntity() ici : construire une ConfigurationVillage a besoin d'aller chercher le Village
// correspondant en base (via VillageRepository), donc cette logique reste dans le service —
// même raisonnement que pour UtilisateurMapper.
public class ConfigurationVillageMapper {

    public static ConfigurationVillageResponseDTO toResponseDto(ConfigurationVillage config) {
        return new ConfigurationVillageResponseDTO(
                config.getId(),
                config.getVillage().getId(), // on ne prend que l'id du village, pas l'objet complet
                config.getMontantCotisationMensuelle(),
                config.getMontantPenaliteRetard(),
                config.getMontantPenaliteAbsenceReunion()
        );
    }
}
