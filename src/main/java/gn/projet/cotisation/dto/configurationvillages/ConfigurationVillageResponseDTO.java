package gn.projet.cotisation.dto.configurationvillages;

import java.math.BigDecimal;

// DTO de RÉPONSE pour ConfigurationVillage. "villageId" référence le village par son id
// plutôt que d'imbriquer un VillageResponseDTO complet — cette config sera toujours consultée
// DANS le contexte d'un village déjà connu (ex: GET /api/villages/{id}/configuration).
public record ConfigurationVillageResponseDTO(
        Long id,
        Long villageId,
        BigDecimal montantCotisationMensuelle,
        BigDecimal montantPenaliteRetard,
        BigDecimal montantPenaliteAbsenceReunion
) {
}
