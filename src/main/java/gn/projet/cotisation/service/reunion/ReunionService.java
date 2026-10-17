package gn.projet.cotisation.service.reunion;

import gn.projet.cotisation.dto.reunions.CreateReunionRequestDTO;
import gn.projet.cotisation.dto.reunions.ReunionResponseDTO;
import gn.projet.cotisation.entity.Utilisateur;

import java.util.List;

public interface ReunionService {
    // Crée une réunion DANS un village précis, pour le compte du
   // président actuellement connecté.
    // Lève VillageNotFoundException (village inexistant) ou
    // VillageAccessDeniedException
    // (village pas géré par ce président) — même vérification que
    // pour createMembre().

    ReunionResponseDTO create(Long villageId, CreateReunionRequestDTO dto, Utilisateur presidentConnecte);

   List<ReunionResponseDTO> findReunionDuVillage(Long villageId, Utilisateur presidentConnecte);

   void delete(Long villageId, Long reunionId, Utilisateur presidentConnecte);
}


