package gn.projet.cotisation.dto.configurationvillages;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

// DTO de REQUÊTE : pas de "villageId" ici — le village concerné viendra de l'URL
// (ex: PUT /api/villages/{villageId}/configuration), pas du corps JSON.
public record UpdateConfigurationVillageRequestDTO(
        @NotNull(message = "Le montant de la cotisation mensuelle est requis")
        @PositiveOrZero(message = "Le montant ne peut pas être négatif")
        BigDecimal montantCotisationMensuelle,
        @NotNull(message = "Le montant de la pénalité de retard est requis")
        @PositiveOrZero(message = "Le montant ne peut pas être négatif")
        BigDecimal montantPenaliteRetard,
        @NotNull(message = "Le montant de la pénalité d'absence est requis")
        @PositiveOrZero(message = "Le montant ne peut pas être négatif")
        BigDecimal montantPenaliteAbsenceReunion
) {
}
