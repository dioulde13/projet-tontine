package gn.projet.cotisation.service.configurationvillage;

import gn.projet.cotisation.dto.configurationvillages.ConfigurationVillageResponseDTO;
import gn.projet.cotisation.dto.configurationvillages.UpdateConfigurationVillageRequestDTO;
import gn.projet.cotisation.entity.Utilisateur;

public interface ConfigurationVillageService {

    // Crée la configuration d'un village. Lève une erreur
  //409 (contrainte unique violée) si une
    // configuration existe déjà pour ce village — un village
    //n'en a qu'une seule (@OneToOne).
    ConfigurationVillageResponseDTO create(Long villageId,
                                           UpdateConfigurationVillageRequestDTO dto,
                                           Utilisateur presidentConnecte);

    // Récupère la configuration d'un village. Lève
    //ConfigurationVillageNotFoundException si elle
    // n'a pas encore été créée.
    ConfigurationVillageResponseDTO findByVillage(Long
                                                          villageId, Utilisateur presidentConnecte);

    // Modifie la configuration existante d'un village. Lève ConfigurationVillageNotFoundException
    // si elle n'a pas encore été créée (il faut passer par create() d'abord).
    ConfigurationVillageResponseDTO update(Long villageId,
                                            UpdateConfigurationVillageRequestDTO dto,
                                            Utilisateur presidentConnecte);
}
