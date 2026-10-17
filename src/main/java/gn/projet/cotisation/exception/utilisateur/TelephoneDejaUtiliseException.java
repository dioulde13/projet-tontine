package gn.projet.cotisation.exception.utilisateur;

public class TelephoneDejaUtiliseException extends RuntimeException {

    public TelephoneDejaUtiliseException(String telephone) {
        super("Le numéro de téléphone '" + telephone + "' est déjà utilisé par un autre utilisateur");
    }
}
