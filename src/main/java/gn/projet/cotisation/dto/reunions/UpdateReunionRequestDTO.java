package gn.projet.cotisation.dto.reunions;

import java.time.LocalDateTime;

public record UpdateReunionRequestDTO(
        Long villageId,
        LocalDateTime dateHeure,
        String lieu
) {
}
