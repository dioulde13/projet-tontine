package gn.projet.cotisation.dto.presences;

import gn.projet.cotisation.dto.reunions.ReunionResponseDTO;
import gn.projet.cotisation.dto.utilisateurs.UtilisateurResponseDTO;

public record PresenceResponseDTO(
        Long id,
        ReunionResponseDTO reunion,       // la réunion complète, pas juste son id
        UtilisateurResponseDTO utilisateur, // le membre concerné
        boolean present
) {
}
