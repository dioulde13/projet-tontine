package gn.projet.cotisation.dto.roles;

// DTO de RÉPONSE pour Role : utilisé en sortie (ce que l'API renvoie au client), avec l'id généré par la base.
public record RoleResponseDTO(
        Long id,
        String nom
) {
}
