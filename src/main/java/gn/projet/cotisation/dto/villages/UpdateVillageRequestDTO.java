package gn.projet.cotisation.dto.villages;

// DTO de REQUÊTE pour Village : utilisé en entrée, à la fois pour créer (POST) et modifier (PUT)
public record UpdateVillageRequestDTO(
        String nom
) {
}
