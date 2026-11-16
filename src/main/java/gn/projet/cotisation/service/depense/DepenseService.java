package gn.projet.cotisation.service.depense;

import gn.projet.cotisation.dto.depenses.CreateDepenseRequestDTO;
import gn.projet.cotisation.dto.depenses.DepenseResponseDTO;
import gn.projet.cotisation.entity.Utilisateur;

import java.util.List;

public interface DepenseService {

    DepenseResponseDTO create(Long villageId, CreateDepenseRequestDTO dto, Utilisateur presidentConnecte);

    List<DepenseResponseDTO> findAllByVillage(Long villageId, Utilisateur presidentConnecte);

    DepenseResponseDTO findOne(Long villageId, Long depenseId, Utilisateur presidentConnecte);

    void delete(Long villageId, Long depenseId, Utilisateur presidentConnecte);
}
