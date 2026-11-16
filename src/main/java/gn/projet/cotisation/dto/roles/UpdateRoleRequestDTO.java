package gn.projet.cotisation.dto.roles;

import jakarta.validation.constraints.NotBlank;

// DTO de REQUÊTE pour Role : utilisé en entrée, à la fois pour créer (POST) et modifier (PUT) un rôle.
// Pas de champ "id" ici : à la création il n'existe pas encore (généré par la base), et à la
// modification il vient déjà de l'URL (/api/roles/{id}), pas besoin de le répéter dans le corps JSON.
public record UpdateRoleRequestDTO(
        @NotBlank(message = "Le nom est requis")
        String nom // ex: "ADMIN", "MEMBRE"
) {
}
