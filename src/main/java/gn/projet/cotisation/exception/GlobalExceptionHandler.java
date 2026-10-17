package gn.projet.cotisation.exception;

import gn.projet.cotisation.exception.configurationvillage.ConfigurationVillageNotFoundException;
import gn.projet.cotisation.exception.paiement.PaiementDejaExistantException;
import gn.projet.cotisation.exception.paiement.PaiementNotFoundException;
import gn.projet.cotisation.exception.penalite.PenaliteNotFoundException;
import gn.projet.cotisation.exception.penalite.TypePenaliteManuelInvalideException;
import gn.projet.cotisation.exception.presence.PresenceNotFoundException;
import gn.projet.cotisation.exception.recu.PaiementsIncompatiblesException;
import gn.projet.cotisation.exception.recu.RecuDejaExistantException;
import gn.projet.cotisation.exception.recu.RecuNotFoundException;
import gn.projet.cotisation.exception.reunion.ReunionNotFoundException;
import gn.projet.cotisation.exception.role.RoleNotFoundException;
import gn.projet.cotisation.exception.utilisateur.TelephoneDejaUtiliseException;
import gn.projet.cotisation.exception.utilisateur.UtilisateurNotFoundException;
import gn.projet.cotisation.exception.utilisateur.UtilisateurSansVillageException;
import gn.projet.cotisation.exception.village.VillageAccessDeniedException;
import gn.projet.cotisation.exception.village.VillageNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException; // classe parente de BadCredentialsException, etc.
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;

// @RestControllerAdvice = un @Component spécial que Spring applique GLOBALEMENT à tous les @RestController
// de l'application. Dès qu'une méthode de contrôleur laisse une exception remonter sans l'attraper,
// Spring cherche ici une méthode @ExceptionHandler qui correspond au type de l'exception.
@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(ConfigurationVillageNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleConfigurationVillageNotFound(ConfigurationVillageNotFoundException ex, WebRequest request) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }


    @ExceptionHandler(PaiementNotFoundException.class)
    public ResponseEntity<ErrorResponse>
    handlePaiementNotFound(PaiementNotFoundException ex, WebRequest request) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    // 409 Conflict : même logique que RecuDejaExistantException plus haut — la requête est valide,
    // mais ce membre a déjà un paiement enregistré pour ce mois/cette année (contrainte unique).
    @ExceptionHandler(PaiementDejaExistantException.class)
    public ResponseEntity<ErrorResponse> handlePaiementDejaExistant(PaiementDejaExistantException ex, WebRequest request) {
        return build(HttpStatus.CONFLICT, ex.getMessage(), request);
    }


    @ExceptionHandler(ReunionNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleReunionNotFound(
            ReunionNotFoundException ex, WebRequest request) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(PresenceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePresenceNotFound(PresenceNotFoundException ex, WebRequest request) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(PenaliteNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePenaliteNotFound(PenaliteNotFoundException ex, WebRequest request) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(RecuNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleRecuNotFound(RecuNotFoundException ex, WebRequest request) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    // 409 Conflict : même logique que TelephoneDejaUtiliseException plus bas — la requête est valide,
    // mais elle entre en conflit avec l'état actuel des données (un reçu existe déjà pour ce paiement).
    @ExceptionHandler(RecuDejaExistantException.class)
    public ResponseEntity<ErrorResponse> handleRecuDejaExistant(RecuDejaExistantException ex, WebRequest request) {
        return build(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    // 400 Bad Request : liste de paiements vide, ou paiements appartenant à des membres différents —
    // dans les deux cas la demande elle-même est mal formée, pas un conflit avec l'état des données.
    @ExceptionHandler(PaiementsIncompatiblesException.class)
    public ResponseEntity<ErrorResponse> handlePaiementsIncompatibles(PaiementsIncompatiblesException ex, WebRequest request) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    // 400 Bad Request : la requête elle-même est mal formée (type de pénalité non autorisé en création
    // manuelle) — contrairement à 404/403/409 qui parlent de l'état des données, ici c'est la demande
    // du client qui est invalide dès le départ.
    @ExceptionHandler(TypePenaliteManuelInvalideException.class)
    public ResponseEntity<ErrorResponse> handleTypePenaliteManuelInvalide(TypePenaliteManuelInvalideException ex, WebRequest request) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    // Déclenché uniquement quand une RoleNotFoundException est levée n'importe où dans l'application
    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleRoleNotFound(RoleNotFoundException ex, WebRequest request) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(UtilisateurNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUtilisateurNotFound(UtilisateurNotFoundException ex, WebRequest request) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    // 400 Bad Request : le compte connecté existe bien (authentifié), mais il lui manque un prérequis
    // (un village assigné) pour que ce endpoint précis ait un sens.
    @ExceptionHandler(UtilisateurSansVillageException.class)
    public ResponseEntity<ErrorResponse> handleUtilisateurSansVillage(UtilisateurSansVillageException ex, WebRequest request) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    @ExceptionHandler(VillageNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleVillageNotFound(VillageNotFoundException ex, WebRequest request) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    // Déclenché quand un PRESIDENT authentifié tente d'agir sur un village qu'il ne gère pas.
    // 403 Forbidden : contrairement au 401 du login, ici on SAIT qui il est, on lui refuse juste l'action.
    @ExceptionHandler(VillageAccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleVillageAccessDenied(VillageAccessDeniedException ex, WebRequest request) {
        return build(HttpStatus.FORBIDDEN, ex.getMessage(), request);
    }

    // 409 Conflict : la requête est valide, mais elle entre en conflit avec l'état actuel des données
    // (ici, un numéro de téléphone déjà pris) — c'est le code HTTP fait pour ce cas précis, contrairement
    // au 400 Bad Request qui signifierait "ta requête elle-même est mal formée".
    @ExceptionHandler(TelephoneDejaUtiliseException.class)
    public ResponseEntity<ErrorResponse> handleTelephoneDejaUtilise(TelephoneDejaUtiliseException ex, WebRequest request) {
        return build(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    // Déclenché quand AuthController.login() appelle authenticationManager.authenticate() avec un numéro
    // de téléphone inconnu ou un mauvais mot de passe (Spring Security lève une BadCredentialsException,
    // sous-classe d'AuthenticationException, qu'on attrape ici de façon générique).
    // 401 Unauthorized = "je ne sais pas qui tu es" (contrairement à 403 Forbidden = "je sais qui tu es, mais interdit").
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException ex, WebRequest request) {
        return build(HttpStatus.UNAUTHORIZED, "Téléphone ou mot de passe incorrect", request);
    }

    // Filet de sécurité : si une contrainte de la base de données est violée sans qu'on l'ait vérifiée
    // nous-mêmes avant (ex: un autre champ unique qu'on aurait oublié de contrôler), on évite quand même
    // de renvoyer un 500 brut avec un stack trace SQL exposé au client.
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex, WebRequest request) {
        String causeMessage = ex.getMostSpecificCause().getMessage(); // le vrai message SQL renvoyé par MySQL
        if (causeMessage != null && causeMessage.toLowerCase().contains("foreign key constraint fails")) {
            return build(HttpStatus.CONFLICT,
                    "Impossible de supprimer : des données liées existent encore (présences, paiements, pénalités...)",
                    request);
        }
        return build(HttpStatus.CONFLICT, "Conflit : une contrainte de données a été violée (valeur en doublon ?)", request);
    }

    // Filet de sécurité final : capture TOUTE exception non prévue par les handlers ci-dessus,
    // pour ne jamais laisser un stack trace Java brut partir dans la réponse HTTP.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, WebRequest request) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Une erreur interne est survenue", request);
    }

    // Méthode d'aide privée : construit l'objet ErrorResponse et l'enveloppe dans le bon code HTTP,
    // pour éviter de répéter cette construction dans chaque méthode @ExceptionHandler ci-dessus.
    private ResponseEntity<ErrorResponse> build(HttpStatus status, String message, WebRequest request) {
        ErrorResponse body = new ErrorResponse(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                // WebRequest.getDescription(false) renvoie quelque chose comme "uri=/api/roles/42"
                request.getDescription(false).replace("uri=", "")
        );
        return ResponseEntity.status(status).body(body);
    }
}
