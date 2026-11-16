package gn.projet.cotisation.dto.utilisateurs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

// DTO de REQUÊTE dédié à la création d'un membre PAR UN PRÉSIDENT, depuis un village déjà choisi.
// Contrairement à UpdateUtilisateurRequestDTO : pas de roleIds (le rôle est toujours MEMBRE,
// fixé par le service, pas par le client) et pas de villageId (il vient de l'URL /villages/{id}/membres).
// Pas de motDePasse : le mot de passe initial est TOUJOURS le numéro de téléphone, fixé par le service
// (le président ne choisit pas le mot de passe du membre qu'il crée).
public record CreateMembreRequestDTO(
        @NotBlank(message = "Le nom est requis")
        String nom,
        @NotBlank(message = "Le prénom est requis")
        String prenom,
        @NotBlank(message = "Le téléphone est requis")
        @Pattern(regexp = "^\\+?[0-9]{8,15}$", message = "Numéro de téléphone invalide")
        String telephone
) {
}
