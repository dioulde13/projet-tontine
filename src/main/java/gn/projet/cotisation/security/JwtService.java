package gn.projet.cotisation.security;

import io.jsonwebtoken.Claims; // les informations contenues dans un token (subject, expiration, etc.)
import io.jsonwebtoken.Jwts; // point d'entrée de jjwt pour construire/parser des tokens
import io.jsonwebtoken.security.Keys; // aide à construire une clé de signature à partir d'octets
import org.springframework.beans.factory.annotation.Value; // injecte une propriété d'application.properties
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

// Centralise tout ce qui touche à la création et à la vérification des tokens JWT :
// le reste de l'application n'a jamais besoin de manipuler directement la bibliothèque jjwt.
@Service
public class JwtService {

    // @Value injecte la valeur de "jwt.secret" définie dans application.properties dans ce champ
    @Value("${jwt.secret}")
    private String secret;

    // Injecte "jwt.expiration-ms" (durée de validité d'un token, en millisecondes)
    @Value("${jwt.expiration-ms}")
    private long expirationMs;

    // Construit la clé de signature HMAC-SHA256 à partir du secret configuré
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)); // convertit le texte en octets, puis en clé
    }

    // Génère un nouveau token pour l'utilisateur donné, valable "expirationMs" millisecondes à partir de maintenant
    public String generateToken(UserDetails userDetails) {
        Date now = new Date(); // instant présent
        Date expiration = new Date(now.getTime() + expirationMs); // instant présent + durée de validité

        return Jwts.builder()
                .subject(userDetails.getUsername())  // "subject" = à qui appartient ce token (ici, le téléphone)
                .issuedAt(now)                         // date de création du token
                .expiration(expiration)                // date au-delà de laquelle le token n'est plus valide
                .signWith(getSigningKey())             // signe le token : garantit qu'il n'a pas été modifié
                .compact();                            // sérialise le tout en une chaîne de caractères (le token final)
    }

    // Extrait le téléphone (subject) contenu dans un token déjà émis
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject); // délègue à extractClaim en demandant le champ "subject"
    }

    // Vérifie qu'un token correspond bien à l'utilisateur donné ET qu'il n'est pas expiré
    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractUsername(token); // téléphone contenu dans le token
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token); // les deux conditions doivent être vraies
    }

    // Compare la date d'expiration du token à l'instant présent
    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date()); // vrai si la date est déjà passée
    }

    // Méthode générique : parse le token (vérifie sa signature au passage), puis applique la fonction donnée
    // pour extraire l'information voulue (subject, date d'expiration...) sans dupliquer le code de parsing.
    private <T> T extractClaim(String token, Function<Claims, T> resolver) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())   // si la signature ne correspond pas à notre clé, exception levée ici
                .build()
                .parseSignedClaims(token)      // décode le token et vérifie sa signature
                .getPayload();                 // récupère les données (Claims) contenues dans le token
        return resolver.apply(claims); // applique la fonction reçue en paramètre (ex: Claims::getSubject)
    }
}
