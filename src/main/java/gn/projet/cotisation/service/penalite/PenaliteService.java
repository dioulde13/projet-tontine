package gn.projet.cotisation.service.penalite;

import gn.projet.cotisation.dto.penalites.PenaliteResponseDTO;
import gn.projet.cotisation.dto.penalites.UpdatePenaliteRequestDTO;
import gn.projet.cotisation.entity.Utilisateur;

import java.util.List;

public interface PenaliteService {

    PenaliteResponseDTO create(Long villageId, UpdatePenaliteRequestDTO dto, Utilisateur presidentConnecte);

    List<PenaliteResponseDTO> findAllPenalitesVillage(Long villageId, Utilisateur presidentConnecte);

    void delete(Long villageId, Long penaliteId, Utilisateur presidentConnecte);

    // Marque une pénalité comme payée (statut NON_PAYEE -> PAYEE)
    PenaliteResponseDTO payer(Long villageId, Long penaliteId, Utilisateur presidentConnecte);
}
