package gn.projet.cotisation.dto.mapper;

import gn.projet.cotisation.dto.presences.PresenceResponseDTO;
import gn.projet.cotisation.entity.Presence;

// Pas de toEntity() : a besoin de résoudre Reunion ET Utilisateur (via leurs repositories respectifs).
public class PresenceMapper {

    public static PresenceResponseDTO toResponseDto(Presence presence) {
        return new PresenceResponseDTO(
                presence.getId(),
                ReunionMapper.toResponseDto(presence.getReunion()),
                UtilisateurMapper.toResponseDto(presence.getUtilisateur()),
                presence.isPresent() // record Java génère un accesseur "present()" en interne, mais l'entité
                                      // expose "isPresent()" comme tout champ boolean généré par Lombok
        );
    }
}
