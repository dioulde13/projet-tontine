package gn.projet.cotisation.dto.mapper;

import gn.projet.cotisation.dto.penalites.PenaliteResponseDTO;
import gn.projet.cotisation.entity.Penalite;

// Pas de toEntity() : a besoin de résoudre Utilisateur (et parfois Paiement/Presence) via leurs repositories.
public class PenaliteMapper {

    public static PenaliteResponseDTO toResponseDto(Penalite penalite) {
        return new PenaliteResponseDTO(
                penalite.getId(),
                UtilisateurMapper.toResponseDto(penalite.getUtilisateur()),
                penalite.getType(),
                penalite.getMontant(),
                penalite.getDate(),
                penalite.getStatut(),
                // ces deux liens sont optionnels sur l'entité : on ne lit l'id que s'ils sont renseignés,
                // sinon on renvoie null (évite un NullPointerException sur getPaiementOrigine().getId())
                penalite.getPaiementOrigine() != null ? penalite.getPaiementOrigine().getId() : null,
                penalite.getPaiementOrigine() != null ? penalite.getPaiementOrigine().getMois() : null,
                penalite.getPaiementOrigine() != null ? penalite.getPaiementOrigine().getAnnee() : null,
                penalite.getPresenceOrigine() != null ? penalite.getPresenceOrigine().getId() : null
        );
    }
}
