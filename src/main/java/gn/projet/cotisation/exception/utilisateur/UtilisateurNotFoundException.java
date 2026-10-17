package gn.projet.cotisation.exception.utilisateur;

public class UtilisateurNotFoundException extends RuntimeException {

    public UtilisateurNotFoundException(Long id) {
        super("Aucun utilisateur trouvé avec l'id " + id);
    }
}
