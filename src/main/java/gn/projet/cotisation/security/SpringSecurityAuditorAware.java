package gn.projet.cotisation.security;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

// Fournit le "qui" de l'audit JPA (createdBy/updatedBy, voir BaseEntity) à Spring Data, qui l'appelle
// automatiquement à chaque création/modification d'une entité qui hérite de BaseEntity.
//
// Ne fait JAMAIS confiance à une valeur fournie par le client : l'identifiant vient uniquement du
// SecurityContext, rempli par JwtAuthenticationFilter après vérification du token — jamais d'un champ
// du corps de la requête. C'est le même identifiant que celui utilisé pour l'authentification elle-même
// (CustomUserDetails.getUsername() = le téléphone, voir aussi JwtService : c'est le "subject" du token).
@Component
public class SpringSecurityAuditorAware implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Spring Security peuple un "utilisateur anonyme" par défaut pour les requêtes non authentifiées
        // (ex: POST /api/utilisateurs, l'inscription publique — voir SecurityConfig). authentication n'est
        // alors jamais null et isAuthenticated() vaut true : il faut explicitement écarter ce cas, sinon
        // createdBy se retrouverait avec la valeur générique "anonymousUser", qui ressemble à tort à un
        // vrai identifiant. Dans ce cas, on renvoie Optional.empty() : la colonne reste NULL, ce qui reflète
        // honnêtement qu'aucun utilisateur authentifié n'est à l'origine de l'opération.
        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return Optional.empty();
        }

        return Optional.of(authentication.getName());
    }
}
