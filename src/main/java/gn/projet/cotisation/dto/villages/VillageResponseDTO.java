package gn.projet.cotisation.dto.villages;

// DTO de RÉPONSE pour Village : ce que l'API renvoie (id inclus, généré par la base)
public record VillageResponseDTO(
        Long id,
        String nom
) {
}
