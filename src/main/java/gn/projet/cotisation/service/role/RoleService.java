package gn.projet.cotisation.service.role; // sous-package "role" du package service

import gn.projet.cotisation.dto.roles.RoleResponseDTO; // DTO renvoyé en sortie (lecture)
import gn.projet.cotisation.dto.roles.UpdateRoleRequestDTO; // DTO reçu en entrée (création/modification)

import java.util.List;

// Interface = le CONTRAT du service. Remarque : findById/update/delete ne renvoient plus d'Optional
// ni de boolean — elles lèvent maintenant RoleNotFoundException quand l'id n'existe pas, interceptée
// automatiquement par GlobalExceptionHandler. Le contrôleur n'a donc plus besoin de gérer le cas "absent".
public interface RoleService {

    List<RoleResponseDTO> findAll(); // renvoie tous les rôles existants, liste vide si aucun

    RoleResponseDTO findById(Long id); // renvoie le rôle demandé ; lève RoleNotFoundException si absent

    RoleResponseDTO create(UpdateRoleRequestDTO dto); // crée un rôle et renvoie sa version enregistrée (avec id)

    RoleResponseDTO update(Long id, UpdateRoleRequestDTO dto); // met à jour ; lève RoleNotFoundException si absent

    void delete(Long id); // supprime ; lève RoleNotFoundException si absent ; rien à renvoyer en cas de succès
}
