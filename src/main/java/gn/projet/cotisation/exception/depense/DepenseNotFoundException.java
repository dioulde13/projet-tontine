package gn.projet.cotisation.exception.depense;

public class DepenseNotFoundException extends RuntimeException {

    public DepenseNotFoundException(Long id) {
        super("Aucune dépense trouvée avec l'id " + id);
    }
}
