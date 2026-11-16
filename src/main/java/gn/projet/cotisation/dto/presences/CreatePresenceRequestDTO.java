package gn.projet.cotisation.dto.presences;

import jakarta.validation.constraints.NotNull;

public record CreatePresenceRequestDTO(
        @NotNull(message = "Le membre est requis")
        Long utilisateurId,
        // boolean primitif (jamais null une fois désérialisé, défaut à false si absent du JSON) :
        // aucune contrainte de validation n'a de sens ici.
        boolean present
) {
}
