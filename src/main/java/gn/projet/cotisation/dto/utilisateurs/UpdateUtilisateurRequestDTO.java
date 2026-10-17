package gn.projet.cotisation.dto.utilisateurs;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;

import java.util.Set;

// DTO de REQUÊTE pour Utilisateur : utilisé en entrée, à la fois pour créer (POST) et modifier (PUT).
//   - motDePasse : IGNORÉ à la création (le mot de passe initial est toujours le numéro de téléphone,
//     fixé par le service) ; pris en compte à la modification SEULEMENT si le téléphone ne change pas
//     (si le téléphone change, le service resynchronise le mot de passe = nouveau téléphone).
//   - roleIds (Set<Long>) au lieu d'objets rôle complets : le client envoie juste les IDs des rôles
//     déjà existants à attribuer (ex: [1, 3]) — plus simple et plus sûr (impossible d'inventer un
//     rôle avec un nom arbitraire au passage).
//   - villageId : rempli si on crée/modifie un MEMBRE (le village auquel il appartient). Null sinon.
//   - villagesGeresIds : rempli si on crée/modifie un PRESIDENT (les villages qu'il gère). Vide sinon.
// Les @Schema(example = ...) ci-dessous ne changent aucun comportement : ils servent uniquement à ce
// que Swagger UI propose null/[] par défaut pour villageId/villagesGeresIds au lieu de 0/[0] (le défaut
// générique de Swagger pour Long/Set<Long>), qui provoquait un 404 "village introuvable" pour un ADMIN.
public record UpdateUtilisateurRequestDTO(
        String nom,
        String prenom,
        @Pattern(regexp = "^\\+?[0-9]{8,15}$", message = "Numéro de téléphone invalide")
        String telephone,
        String motDePasse,
        Set<Long> roleIds,
        @Schema(example = "null", description = "Id du village du MEMBRE. Laisser null pour ADMIN/PRESIDENT.")
        Long villageId,
        @ArraySchema(
                schema = @Schema(implementation = Long.class),
                arraySchema = @Schema(example = "[]", description = "Ids des villages gérés par le PRESIDENT. Laisser vide pour ADMIN/MEMBRE.")
        )
        Set<Long> villagesGeresIds
) {
}
