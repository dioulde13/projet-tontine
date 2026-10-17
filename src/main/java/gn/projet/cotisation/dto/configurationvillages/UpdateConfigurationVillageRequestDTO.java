package gn.projet.cotisation.dto.configurationvillages;

import java.math.BigDecimal;

// DTO de REQUÊTE : pas de "villageId" ici — le village concerné viendra de l'URL
// (ex: PUT /api/villages/{villageId}/configuration), pas du corps JSON.
public record UpdateConfigurationVillageRequestDTO(
        BigDecimal montantCotisationMensuelle,
        BigDecimal montantPenaliteRetard,
        BigDecimal montantPenaliteAbsenceReunion
) {
}
