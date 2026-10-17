package gn.projet.cotisation.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import gn.projet.cotisation.exception.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler; // contrat Spring Security pour ce cas précis
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;

// Déclenché par Spring Security quand l'utilisateur EST authentifié (token valide), mais que son rôle
// ne lui donne pas le droit d'accéder à cette route précise (ex: une route réservée à @PreAuthorize("hasRole('ADMIN')")
// appelée par un simple MEMBRE). Pas encore utilisé activement tant qu'aucune règle de rôle n'est posée,
// mais nécessaire dès qu'on en ajoutera une — sinon ce cas-là renverrait, lui aussi, un 403 vide.
@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper = new ObjectMapper()
            .findAndRegisterModules(); // support d'Instant utilisé dans ErrorResponse

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException {
        ErrorResponse body = new ErrorResponse(
                Instant.now(),
                HttpStatus.FORBIDDEN.value(),        // 403
                HttpStatus.FORBIDDEN.getReasonPhrase(), // "Forbidden"
                "Accès refusé : vous n'avez pas les droits nécessaires pour cette action",
                request.getRequestURI()
        );

        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
