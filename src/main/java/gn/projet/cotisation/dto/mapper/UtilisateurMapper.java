package gn.projet.cotisation.dto.mapper;

import gn.projet.cotisation.dto.utilisateurs.UtilisateurResponseDTO;
import gn.projet.cotisation.entity.Utilisateur;

import java.util.stream.Collectors;

// Convertit une entité Utilisateur en UtilisateurResponseDTO pour les réponses de l'API.
// Pas de méthode "toEntity" ici : construire un Utilisateur a besoin d'aller chercher les Role
// et Village correspondants en base (via leurs repositories), donc cette logique reste dans le
// service plutôt que dans ce mapper qui n'a pas accès à un repository.
public class UtilisateurMapper {

    public static UtilisateurResponseDTO toResponseDto(Utilisateur utilisateur) {
        return new UtilisateurResponseDTO(
                utilisateur.getId(),
                utilisateur.getNom(),
                utilisateur.getPrenom(),
                utilisateur.getTelephone(),
                // on convertit chaque Role de la collection en RoleResponseDTO, puis on regroupe
                // le tout dans un nouveau Set avec collect(Collectors.toSet())
                utilisateur.getRoles().stream()
                        .map(RoleMapper::toResponseDto)
                        .collect(Collectors.toSet()),
                // le champ "village" de l'entité est nullable (vide pour un ADMIN/PRESIDENT) :
                // on ne convertit que s'il est renseigné, sinon on propage simplement null
                utilisateur.getVillage() != null
                        ? VillageMapper.toResponseDto(utilisateur.getVillage())
                        : null,
                // même principe que pour "roles" : chaque Village géré converti en VillageResponseDTO
                utilisateur.getVillagesGeres().stream()
                        .map(VillageMapper::toResponseDto)
                        .collect(Collectors.toSet())
        );
    }
}
