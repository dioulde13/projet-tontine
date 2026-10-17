package gn.projet.cotisation.service.recu;

import gn.projet.cotisation.dto.recus.CreateRecuRequestDTO;
import gn.projet.cotisation.dto.recus.RecuResponseDTO;
import gn.projet.cotisation.entity.Utilisateur;

import java.util.List;

public interface RecuService {

    RecuResponseDTO create(Long villageId, CreateRecuRequestDTO dto, Utilisateur presidentConnecte);

    List<RecuResponseDTO> findRecusByVillage(Long villageId, Utilisateur presidentConnecte);

    RecuResponseDTO findOne(Long villageId, Long recuId, Utilisateur presidentConnecte);
}
