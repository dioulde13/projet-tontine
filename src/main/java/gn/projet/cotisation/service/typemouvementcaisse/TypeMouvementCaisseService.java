package gn.projet.cotisation.service.typemouvementcaisse;

import gn.projet.cotisation.dto.typemouvementcaisse.TypeMouvementCaisseResponseDTO;
import gn.projet.cotisation.dto.typemouvementcaisse.UpdateTypeMouvementCaisseRequestDTO;
import gn.projet.cotisation.entity.Utilisateur;

import java.util.List;

public interface TypeMouvementCaisseService {

    List<TypeMouvementCaisseResponseDTO> findAllByVillage(Long villageId, Utilisateur presidentConnecte);

    TypeMouvementCaisseResponseDTO create(Long villageId, UpdateTypeMouvementCaisseRequestDTO dto, Utilisateur presidentConnecte);

    TypeMouvementCaisseResponseDTO update(Long villageId, Long typeId, UpdateTypeMouvementCaisseRequestDTO dto, Utilisateur presidentConnecte);

    void delete(Long villageId, Long typeId, Utilisateur presidentConnecte);
}
