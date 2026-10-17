package gn.projet.cotisation.service.paiement;

import gn.projet.cotisation.dto.paiements.CreatePaiementRequestDTO;
import gn.projet.cotisation.dto.paiements.PaiementResponseDTO;
import gn.projet.cotisation.entity.Utilisateur;

import java.util.List;

public interface PaiementServices {

    // Un Paiement PAR mois demandé -> la liste renvoyée a autant d'éléments que dto.mois().size()
    List<PaiementResponseDTO> create(Long villageId, CreatePaiementRequestDTO dto, Utilisateur presidentConnecte);

    List<PaiementResponseDTO> findPaiementByVillage(Long villageId, Utilisateur presidentConnecte);

    void delete(Long villageId, Long paiementId, Utilisateur presidentConnecte);
}
