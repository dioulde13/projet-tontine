package gn.projet.cotisation.security;

import io.jsonwebtoken.JwtException; // classe parente de toutes les erreurs jjwt (signature invalide, token malformé, expiré...)
import jakarta.servlet.FilterChain; // représente la suite du traitement de la requête après ce filtre
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken; // objet "je suis authentifié" de Spring Security
import org.springframework.security.core.context.SecurityContextHolder; // stocke qui est authentifié pour la requête en cours
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource; // ajoute des infos techniques (IP, session...)
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter; // garantit une seule exécution par requête HTTP

import java.io.IOException;

// OncePerRequestFilter garantit que ce filtre s'exécute une seule fois par requête HTTP entrante,
// AVANT que la requête n'atteigne le contrôleur. C'est ici qu'on transforme un header "Authorization"
// contenant un token valide en une authentification reconnue par Spring Security pour le reste du traitement.
@Component // enregistré comme bean Spring, injectable dans SecurityConfig
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService; // pour extraire/valider le contenu des tokens
    private final CustomUserDetailsService userDetailsService; // pour recharger l'utilisateur correspondant

    public JwtAuthenticationFilter(JwtService jwtService, CustomUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    // Méthode appelée automatiquement par Spring pour CHAQUE requête HTTP entrante
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization"); // ex: "Bearer eyJhbGciOi..." ou null si absent

        // Pas de header, ou il ne commence pas par "Bearer " -> rien à faire ici, on laisse passer la requête
        // telle quelle (elle sera rejetée plus tard par Spring Security si la route exige d'être connecté).
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response); // passe la main au filtre/contrôleur suivant
            return; // on arrête ici, pas d'authentification à faire
        }

        String token = authHeader.substring(7); // retire les 7 caractères de "Bearer " pour isoler le token

        // Un token corrompu, mal formé, ou signé avec une autre clé fait lever une JwtException à jjwt
        // (dans extractUsername ou plus loin dans isTokenValid). On l'attrape ici pour laisser la requête
        // continuer SANS authentification, au lieu de laisser l'exception remonter et déclencher une
        // redirection interne Spring vers "/error" (qui perdrait le chemin d'origine dans la réponse 401).
        try {
            String telephone = jwtService.extractUsername(token); // décode le token pour en extraire le téléphone (subject)

            // On n'authentifie que si un téléphone a bien été extrait ET si personne n'est déjà authentifié
            // sur cette requête (évite de refaire le travail inutilement).
            if (telephone != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(telephone); // recharge l'utilisateur en base

                if (jwtService.isTokenValid(token, userDetails)) { // vérifie signature + expiration + correspondance
                    // Construit l'objet "Authentication" que Spring Security comprend, avec les rôles de l'utilisateur.
                    // Le 2e argument (null) serait le mot de passe : inutile ici, l'authentification est déjà prouvée par le token.
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request)); // ajoute IP/session
                    // Place cette authentification dans le contexte de sécurité : à partir de maintenant,
                    // pour CETTE requête, Spring Security considère l'utilisateur comme connecté avec ses rôles.
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (JwtException ignored) {
            // Token invalide (signature incorrecte, malformé, expiré...) : on ne fait rien de spécial ici,
            // SecurityContextHolder reste vide -> la requête continue "non authentifiée" -> c'est
            // RestAuthenticationEntryPoint, plus loin, qui décidera de la réponse 401 appropriée.
        }

        filterChain.doFilter(request, response); // dans tous les cas, on continue vers la suite du traitement
    }
}
