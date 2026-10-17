package gn.projet.cotisation.service.presence;

import gn.projet.cotisation.dto.presences.CreatePresenceRequestDTO;
import gn.projet.cotisation.dto.presences.PresenceResponseDTO;
import gn.projet.cotisation.entity.Utilisateur;

import java.util.List;

// Pas d'annotation ici : les interfaces n'ont pas besoin d'être annotées pour Spring —
// c'est leur IMPLÉMENTATION (PresenceServiceImpl, annotée @Service) qui devient le vrai bean.
// @Repository (ce qu'il y avait avant) est de toute façon le mauvais choix sémantique ici :
// cette annotation désigne une classe d'accès aux données, pas un contrat de service métier.
public interface PresenceService {

    PresenceResponseDTO create(Long villageId, Long reunionId, CreatePresenceRequestDTO dto, Utilisateur presidentConnecte);

List<PresenceResponseDTO> findPresents(Long villageId, Long reunionId, Utilisateur presidentConnecte);

List<PresenceResponseDTO> findAbsences(Long villageId, Long reunionId, Utilisateur presidentConnecte);

void delete(Long villageId, Long reunionId, Long presenceId, Utilisateur presidentConnecte);

}
