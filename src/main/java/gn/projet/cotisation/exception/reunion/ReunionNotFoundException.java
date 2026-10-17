package gn.projet.cotisation.exception.reunion;

public class ReunionNotFoundException extends RuntimeException {
    public ReunionNotFoundException(Long id) {
        super("Aucune réunion trouvée avec l'id " + id);
    }
}
