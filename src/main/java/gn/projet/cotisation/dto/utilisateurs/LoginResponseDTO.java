package gn.projet.cotisation.dto.utilisateurs;

// DTO de RÉPONSE pour la connexion : ce que l'API renvoie si telephone/motDePasse sont corrects.
// Le client devra renvoyer ce token dans le header "Authorization: Bearer <token>" sur ses requêtes suivantes.
public record LoginResponseDTO(String token) {
}
