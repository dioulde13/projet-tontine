package gn.projet.cotisation.config;

// Classe qui représente le document OpenAPI complet (toute la description de l'API)
import io.swagger.v3.oas.models.OpenAPI;
// Regroupe les éléments réutilisables du document OpenAPI (ici, le schéma d'authentification)
import io.swagger.v3.oas.models.Components;
// Classe qui représente juste le bloc "métadonnées" du document (titre, description, version)
import io.swagger.v3.oas.models.info.Info;
// Indique quel schéma de sécurité s'applique globalement aux routes de l'API
import io.swagger.v3.oas.models.security.SecurityRequirement;
// Décrit CE QU'EST le mécanisme de sécurité (ici : un token porté dans le header Authorization)
import io.swagger.v3.oas.models.security.SecurityScheme;
// Annotation pour déclarer un bean géré par Spring
import org.springframework.context.annotation.Bean;
// Annotation pour dire à Spring que cette classe contient des définitions de beans
import org.springframework.context.annotation.Configuration;

// Dit à Spring de lire cette classe au démarrage pour y trouver des beans à créer
@Configuration
public class OpenApiConfig {

    // Nom interne donné à notre schéma de sécurité, réutilisé dans les deux blocs ci-dessous
    private static final String BEARER_SCHEME_NAME = "bearerAuth";

    // Dit à Spring d'exécuter cette méthode et de garder le résultat comme bean disponible
    // dans toute l'application. Springdoc va automatiquement utiliser ce bean OpenAPI
    // personnalisé à la place de ses métadonnées génériques par défaut.
    @Bean
    public OpenAPI cotisationOpenAPI() {
        // On construit l'objet OpenAPI et on lui attache un bloc Info
        return new OpenAPI()
                .info(new Info()
                        .title("Cotisation API")             // titre affiché en haut de Swagger UI
                        .description("API de gestion des cotisations") // sous-texte descriptif
                        .version("v1.0"))                     // numéro de version affiché (informatif)
                // Applique le schéma de sécurité "bearerAuth" à TOUTES les routes de la documentation :
                // ça fait apparaître le cadenas et le bouton "Authorize" en haut de Swagger UI.
                .addSecurityItem(new SecurityRequirement().addList(BEARER_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(BEARER_SCHEME_NAME, new SecurityScheme()
                                .name(BEARER_SCHEME_NAME)
                                .type(SecurityScheme.Type.HTTP)   // type HTTP générique (par opposition à API key, OAuth2...)
                                .scheme("bearer")                  // précise le sous-type : "Bearer <token>"
                                .bearerFormat("JWT")));             // indication informative : le token est un JWT
    }
}
