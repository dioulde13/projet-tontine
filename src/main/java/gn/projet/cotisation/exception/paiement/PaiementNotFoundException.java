package gn.projet.cotisation.exception.paiement;

public class PaiementNotFoundException extends RuntimeException {
    public PaiementNotFoundException(Long id) {
        super("Aucun paiement trouvé avec l'id " + id);
    }
}
