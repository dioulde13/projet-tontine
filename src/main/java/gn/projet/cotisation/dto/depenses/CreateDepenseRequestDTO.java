package gn.projet.cotisation.dto.depenses;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateDepenseRequestDTO(
        @NotNull(message = "Le type de dépense est requis")
        Long typeId,
        @NotNull(message = "Le montant est requis")
        @Positive(message = "Le montant doit être supérieur à zéro")
        BigDecimal montant,
        // Motif libre : vient préciser le type choisi, mais reste entièrement optionnel.
        @Size(max = 255, message = "Le motif ne peut pas dépasser 255 caractères")
        String motif,
        @NotNull(message = "La date est requise")
        LocalDate date
) {
}
