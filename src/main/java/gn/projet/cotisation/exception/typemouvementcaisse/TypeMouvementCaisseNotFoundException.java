package gn.projet.cotisation.exception.typemouvementcaisse;

public class TypeMouvementCaisseNotFoundException extends RuntimeException {

    public TypeMouvementCaisseNotFoundException(Long id) {
        super("Aucun type de dépense trouvé avec l'id " + id);
    }
}
