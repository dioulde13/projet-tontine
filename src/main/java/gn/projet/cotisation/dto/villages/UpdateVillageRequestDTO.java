package gn.projet.cotisation.dto.villages;

import jakarta.validation.constraints.NotBlank;

// DTO de REQUÊTE pour Village : utilisé en entrée, à la fois pour créer (POST) et modifier (PUT)
public record UpdateVillageRequestDTO(
        @NotBlank(message = "Le nom est requis")
        String nom
) {
}
