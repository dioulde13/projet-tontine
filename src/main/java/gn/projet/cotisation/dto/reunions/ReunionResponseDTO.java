package gn.projet.cotisation.dto.reunions;

import gn.projet.cotisation.dto.villages.VillageResponseDTO;

import java.time.LocalDateTime;

public record ReunionResponseDTO(
        Long id,
        VillageResponseDTO village,
        LocalDateTime dateHeure,
        String lieu
) {
}
