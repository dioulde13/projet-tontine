package gn.projet.cotisation.dto.presences;

// present = false déclenchera automatiquement une Penalite ABSENCE_REUNION (logique posée au niveau service)
public record UpdatePresenceRequestDTO(
        Long reunionId,
        Long utilisateurId,
        boolean present
) {
}
