package gn.projet.cotisation.exception.recu;

public class RecuNotFoundException extends RuntimeException {
    public RecuNotFoundException(Long id) {
        super("Aucun reçu trouvé avec l'id " + id);
    }
}
