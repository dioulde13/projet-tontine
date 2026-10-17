package gn.projet.cotisation.exception.recu;

// Levée quand on tente de regrouper dans UN SEUL reçu des paiements qui n'appartiennent pas tous
// au même membre (un reçu quittance toujours un seul membre), ou quand la liste de paiements
// fournie est vide.
public class PaiementsIncompatiblesException extends RuntimeException {

    public PaiementsIncompatiblesException(String message) {
        super(message);
    }
}
