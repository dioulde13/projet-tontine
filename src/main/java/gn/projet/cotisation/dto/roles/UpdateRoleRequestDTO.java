package gn.projet.cotisation.dto.roles;

// DTO de REQUÊTE pour Role : utilisé en entrée, à la fois pour créer (POST) et modifier (PUT) un rôle.
// Pas de champ "id" ici : à la création il n'existe pas encore (généré par la base), et à la
// modification il vient déjà de l'URL (/api/roles/{id}), pas besoin de le répéter dans le corps JSON.
public record UpdateRoleRequestDTO(
        String nom // ex: "ADMIN", "MEMBRE"
) {
}
