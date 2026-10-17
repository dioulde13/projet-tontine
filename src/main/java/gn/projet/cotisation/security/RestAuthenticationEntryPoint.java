package gn.projet.cotisation.security;

import com.fasterxml.jackson.databind.ObjectMapper; // sérialise notre ErrorResponse en JSON manuellement
import gn.projet.cotisation.exception.ErrorResponse; // même format d'erreur que le reste de l'API
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint; // contrat Spring Security pour ce cas précis
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;

// Déclenché par Spring Security quand une route protégée est appelée SANS authentification valide
// du tout (aucun token, ou token invalide/expiré). Ce point d'entrée s'exécute AVANT tout contrôleur,
// donc GlobalExceptionHandler ne peut pas intervenir ici — c'est pour ça qu'on doit écrire nous-mêmes
// une réponse JSON cohérente avec le reste de l'API, au lieu de laisser Spring renvoyer un corps vide.
@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper() // convertit un objet Java en JSON
            .findAndRegisterModules(); // active le support des types Java 8+ comme Instant (utilisé par ErrorResponse)

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException // la raison technique du rejet (non utilisée dans le message, volontairement vague pour la sécurité)
    ) throws IOException {
        ErrorResponse body = new ErrorResponse(
                Instant.now(),
                HttpStatus.UNAUTHORIZED.value(),        // 401
                HttpStatus.UNAUTHORIZED.getReasonPhrase(), // "Unauthorized"
                "Authentification requise : token manquant, invalide ou expiré",
                request.getRequestURI() // chemin de la requête rejetée, ex: "/api/roles"
        );

        response.setStatus(HttpStatus.UNAUTHORIZED.value()); // fixe le code HTTP de la réponse
        response.setContentType("application/json;charset=UTF-8"); // dit au client que le corps est du JSON
        response.getWriter().write(objectMapper.writeValueAsString(body)); // écrit le JSON dans le corps de la réponse
    }
}
