package gn.projet.cotisation.dto.utilisateurs;

import gn.projet.cotisation.dto.roles.RoleResponseDTO;
import gn.projet.cotisation.dto.villages.VillageResponseDTO;

import java.util.Set;

// DTO de RÉPONSE pour Utilisateur : utilisé en sortie (GET, ou après création/modification).
// Volontairement PAS de champ motDePasse : on ne renvoie jamais le mot de passe dans une réponse JSON.
public record UtilisateurResponseDTO(
        Long id,
        String nom,
        String prenom,
        String telephone,
        Set<RoleResponseDTO> roles,        // rôles imbriqués en version DTO de réponse, pas les entités Role brutes
        VillageResponseDTO village,        // rempli si role MEMBRE, sinon null (même principe que roles ci-dessus)
        Set<VillageResponseDTO> villagesGeres // rempli si role PRESIDENT, ensemble vide sinon
) {
}
