package gn.projet.cotisation.dto.mapper;

import gn.projet.cotisation.dto.configurationvillages.ConfigurationVillageResponseDTO;
import gn.projet.cotisation.entity.ConfigurationVillage;

// Pas de toEntity() ici : construire une ConfigurationVillage a besoin d'aller chercher le Village
// correspondant en base (via VillageRepository), donc cette logique reste dans le service —
// même raisonnement que pour UtilisateurMapper.
public class ConfigurationVillageMapper {

    // modifieParNom : nom complet déjà résolu par le service (seul endroit ayant accès à
    // UtilisateurRepository) depuis config.getUpdatedBy() ; null si inconnu ou jamais modifiée.
    public static ConfigurationVillageResponseDTO toResponseDto(ConfigurationVillage config, String modifieParNom) {
        return new ConfigurationVillageResponseDTO(
                config.getId(),
                config.getVillage().getId(), // on ne prend que l'id du village, pas l'objet complet
                config.getMontantCotisationMensuelle(),
                config.getMontantPenaliteRetard(),
                config.getMontantPenaliteAbsenceReunion(),
                config.getUpdatedAt(),
                config.getUpdatedBy(),
                modifieParNom
        );
    }
}
