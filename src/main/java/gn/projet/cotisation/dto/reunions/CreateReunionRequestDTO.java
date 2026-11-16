package gn.projet.cotisation.dto.reunions;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CreateReunionRequestDTO(
        @NotNull(message = "La date et l'heure sont requises")
        LocalDateTime dateHeure,
        @NotBlank(message = "Le lieu est requis")
        String lieu
) {
}
