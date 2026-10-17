package gn.projet.cotisation.dto.presences;

public record CreatePresenceRequestDTO(
        Long utilisateurId,
        boolean present
) {
}
