package gn.projet.cotisation.dto.utilisateurs;

import jakarta.validation.constraints.NotBlank;

// DTO de REQUÊTE pour la connexion : ce que le client envoie à POST /api/auth/login
// Volontairement PAS de @Pattern sur le téléphone ici (contrairement à UpdateUtilisateurRequestDTO) :
// un identifiant mal formé doit échouer exactement comme un identifiant inconnu (401 "incorrect"),
// jamais avec un message distinct qui aiderait à deviner le format attendu des comptes existants.
public record LoginRequestDTO(
        @NotBlank(message = "Le téléphone est requis")
        String telephone,
        @NotBlank(message = "Le mot de passe est requis")
        String motDePasse // envoyé en clair par le client UNE fois, jamais stocké ainsi (comparé au hash en base)
) {
}
