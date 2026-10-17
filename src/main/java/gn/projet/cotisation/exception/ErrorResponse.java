package gn.projet.cotisation.exception;

import java.time.Instant;

// Format JSON unique renvoyé pour TOUTE erreur de l'API, quelle que soit l'exception d'origine.
// Avoir une structure d'erreur cohérente est important côté client : il sait toujours où
// trouver le message d'erreur, peu importe l'endpoint appelé.
public record ErrorResponse(
        Instant timestamp, // date/heure exacte de l'erreur, utile pour corréler avec les logs serveur
        int status,        // code HTTP numérique, ex: 404
        String error,      // libellé du code HTTP, ex: "Not Found"
        String message,    // message lisible expliquant la cause précise (ex: "Aucun rôle trouvé avec l'id 42")
        String path         // URL appelée au moment de l'erreur, ex: "/api/roles/42"
) {
}
