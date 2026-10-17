package gn.projet.cotisation.dto.reunions;

import java.time.LocalDateTime;

public record CreateReunionRequestDTO(
        LocalDateTime dateHeure,
        String lieu
) {
}
