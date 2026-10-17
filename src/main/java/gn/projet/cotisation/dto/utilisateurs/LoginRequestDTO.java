package gn.projet.cotisation.dto.utilisateurs;

// DTO de REQUÊTE pour la connexion : ce que le client envoie à POST /api/auth/login
public record LoginRequestDTO(
        String telephone,
        String motDePasse // envoyé en clair par le client UNE fois, jamais stocké ainsi (comparé au hash en base)
) {
}
