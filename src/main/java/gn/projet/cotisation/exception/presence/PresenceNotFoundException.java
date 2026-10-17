package gn.projet.cotisation.exception.presence;

public class PresenceNotFoundException extends RuntimeException {

    public PresenceNotFoundException(Long id) {
        super("Aucune présence trouvée avec l'id " + id);
    }
}
