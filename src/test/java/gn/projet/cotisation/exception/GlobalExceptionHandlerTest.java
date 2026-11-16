package gn.projet.cotisation.exception;

import gn.projet.cotisation.controller.utilisateur.AuthController;
import gn.projet.cotisation.dto.utilisateurs.LoginRequestDTO;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import java.lang.reflect.Method;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

// Teste directement la logique d'agrégation des erreurs de validation (handleValidationException),
// sans contexte Spring complet : plus rapide, et ça isole cette logique précise de tout le reste
// (le câblage réel @Valid -> cette méthode est lui vérifié par ValidationIntegrationTest).
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleValidationException_agregeTousLesChampsEnFauteDansUnMessageUnique() throws NoSuchMethodException {
        MethodArgumentNotValidException ex = exceptionAvecErreurs(
                new FieldError("loginRequestDTO", "telephone", "Le téléphone est requis"),
                new FieldError("loginRequestDTO", "motDePasse", "Le mot de passe est requis")
        );
        WebRequest request = mock(WebRequest.class);
        when(request.getDescription(false)).thenReturn("uri=/api/auth/login");

        var response = handler.handleValidationException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(org.springframework.http.HttpStatusCode.valueOf(400));
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        // Triés par nom de champ (motDePasse avant telephone), indépendamment de l'ordre fourni ci-dessus :
        // garantit un message stable d'une requête à l'autre pour les mêmes erreurs.
        assertThat(response.getBody().message())
                .isEqualTo("motDePasse : Le mot de passe est requis ; telephone : Le téléphone est requis");
        assertThat(response.getBody().path()).isEqualTo("/api/auth/login");
    }

    @Test
    void handleValidationException_gereUneSeuleErreurDeChamp() throws NoSuchMethodException {
        MethodArgumentNotValidException ex = exceptionAvecErreurs(
                new FieldError("loginRequestDTO", "telephone", "Le téléphone est requis")
        );
        WebRequest request = mock(WebRequest.class);
        when(request.getDescription(false)).thenReturn("uri=/api/auth/login");

        var response = handler.handleValidationException(ex, request);

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).isEqualTo("telephone : Le téléphone est requis");
    }

    private MethodArgumentNotValidException exceptionAvecErreurs(FieldError... erreurs) throws NoSuchMethodException {
        Method method = AuthController.class.getMethod("login", LoginRequestDTO.class);
        MethodParameter methodParameter = new MethodParameter(method, 0);
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(erreurs));
        return new MethodArgumentNotValidException(methodParameter, bindingResult);
    }
}
