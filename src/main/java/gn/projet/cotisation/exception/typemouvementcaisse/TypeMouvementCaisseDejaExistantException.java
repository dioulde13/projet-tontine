package gn.projet.cotisation.exception.typemouvementcaisse;

public class TypeMouvementCaisseDejaExistantException extends RuntimeException {

    public TypeMouvementCaisseDejaExistantException(String libelle) {
        super("Un type de dépense nommé \"" + libelle + "\" existe déjà pour ce village");
    }
}
