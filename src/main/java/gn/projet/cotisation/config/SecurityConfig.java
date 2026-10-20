package gn.projet.cotisation.config;

import gn.projet.cotisation.security.CustomUserDetailsService;
import gn.projet.cotisation.security.JwtAuthenticationFilter;
import gn.projet.cotisation.security.RestAccessDeniedHandler; // 403 en JSON (authentifié, mais droits insuffisants)
import gn.projet.cotisation.security.RestAuthenticationEntryPoint; // 401 en JSON (pas authentifié du tout)
import org.springframework.context.annotation.Bean; // déclare un bean géré par Spring
import org.springframework.context.annotation.Configuration; // classe de configuration
import org.springframework.http.HttpMethod; // GET, POST, PUT, DELETE...
import org.springframework.security.authentication.AuthenticationManager; // orchestre le processus d'authentification
import org.springframework.security.authentication.AuthenticationProvider; // sait vérifier UNE identité (ici via UserDetailsService + mot de passe)
import org.springframework.security.authentication.dao.DaoAuthenticationProvider; // implémentation standard basée sur une base de données
import org.springframework.security.config.Customizer; // active un module de sécurité avec sa config par défaut (ex: .cors(Customizer.withDefaults()))
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration; // fournit l'AuthenticationManager de Spring
import org.springframework.security.config.annotation.web.builders.HttpSecurity; // API pour construire les règles de sécurité HTTP
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity; // active la config de sécurité web personnalisée
import org.springframework.security.config.http.SessionCreationPolicy; // politique de création de session HTTP
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // implémentation standard de hachage de mot de passe
import org.springframework.security.crypto.password.PasswordEncoder; // contrat pour hacher/vérifier un mot de passe
import org.springframework.security.web.SecurityFilterChain; // résultat final : la chaîne de filtres de sécurité
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter; // filtre standard de Spring Security, utilisé comme repère de position
import org.springframework.web.cors.CorsConfiguration; // règles CORS (origines/méthodes/en-têtes autorisés)
import org.springframework.web.cors.CorsConfigurationSource; // contrat : fournit la config CORS applicable à une requête
import org.springframework.web.cors.UrlBasedCorsConfigurationSource; // implémentation standard, par motif d'URL

import java.util.List;

// @EnableWebSecurity active la configuration de sécurité personnalisée de Spring Security pour cette application.
@EnableWebSecurity
@Configuration
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService; // pour charger un utilisateur par téléphone
    private final JwtAuthenticationFilter jwtAuthenticationFilter; // notre filtre qui lit les tokens JWT
    private final RestAuthenticationEntryPoint authenticationEntryPoint; // réponse JSON en cas de 401
    private final RestAccessDeniedHandler accessDeniedHandler; // réponse JSON en cas de 403

    public SecurityConfig(
            CustomUserDetailsService userDetailsService,
            JwtAuthenticationFilter jwtAuthenticationFilter,
            RestAuthenticationEntryPoint authenticationEntryPoint,
            RestAccessDeniedHandler accessDeniedHandler
    ) {
        this.userDetailsService = userDetailsService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
    }

    // Bean utilisé partout où on doit hacher ou vérifier un mot de passe (BCrypt = standard actuel, avec "sel" intégré)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Relie CustomUserDetailsService (comment charger un utilisateur) et le PasswordEncoder (comment vérifier
    // son mot de passe) au mécanisme d'authentification standard de Spring Security.
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService); // "Dao" = interroge une base de données
        provider.setPasswordEncoder(passwordEncoder()); // lui donne le moyen de vérifier le mot de passe haché
        return provider;
    }

    // Composant central que AuthController utilisera pour vérifier téléphone + mot de passe lors du login.
    // On délègue sa construction à Spring plutôt que de l'instancier nous-mêmes (config interne complexe).
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // Autorise le frontend Angular (servi sur une autre origine : localhost:4200 en dev, par exemple)
    // à appeler cette API. Sans ça, le navigateur bloque toute requête cross-origin lui-même, AVANT
    // même qu'elle n'atteigne un contrôleur (aucun Access-Control-Allow-Origin dans la réponse) —
    // ce bean est lu automatiquement par .cors(Customizer.withDefaults()) ci-dessous.
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:4200"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        // Pas de cookies/session ici : l'authentification se fait via un header Authorization (JWT),
        // jamais via des credentials de navigateur (cookies) — inutile donc de les autoriser.
        config.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // s'applique à toutes les routes de l'API
        return source;
    }

    // Définit les règles de sécurité HTTP : quelles routes sont publiques, lesquelles exigent d'être connecté.
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Active le CORS Spring Security en lisant le bean CorsConfigurationSource ci-dessus.
                // Doit être activé ICI (pas seulement comme @Bean) pour que la requête de préflight
                // OPTIONS soit traitée AVANT les règles d'authentification plus bas (sinon elle recevrait
                // un 401, comme n'importe quelle autre requête non authentifiée).
                .cors(Customizer.withDefaults())
                // CSRF protège contre des attaques liées aux sessions/cookies ; inutile ici car on est
                // en JWT "stateless" (aucun cookie de session), donc on le désactive explicitement.
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // login : seule la méthode POST est publique sur cette route
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        // inscription : seule la création (POST) est publique ; lister/modifier/supprimer
                        // des utilisateurs reste protégé par "anyRequest().authenticated()" plus bas
                        .requestMatchers(HttpMethod.POST, "/api/utilisateurs").permitAll()
                        // documentation Swagger accessible sans authentification, pratique en développement
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/api/roles/**"
                        ).permitAll()
                        // absolument tout le reste exige d'être authentifié (avec un token JWT valide)
                        .anyRequest().authenticated()
                )
                // STATELESS : on dit explicitement à Spring de ne JAMAIS créer de session HTTP côté serveur —
                // cohérent avec le choix JWT, où chaque requête s'authentifie elle-même via son propre token.
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Remplace les réponses par défaut (vides) de Spring Security par nos réponses JSON cohérentes
                // avec le reste de l'API, pour les deux cas de rejet possibles AVANT même d'atteindre un contrôleur.
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(authenticationEntryPoint) // pas authentifié du tout -> 401 JSON
                        .accessDeniedHandler(accessDeniedHandler)           // authentifié mais droits insuffisants -> 403 JSON
                )
                .authenticationProvider(authenticationProvider()) // branche notre logique de vérification d'identité
                // Insère notre filtre JWT AVANT le filtre standard de Spring Security, pour qu'il authentifie
                // la requête (en lisant le header Authorization) avant que Spring ne décide d'autoriser ou non.
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build(); // construit la chaîne de filtres finale à partir de toute cette configuration
    }
}
