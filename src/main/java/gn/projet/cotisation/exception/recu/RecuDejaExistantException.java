package gn.projet.cotisation.exception.recu;

// Levée quand on essaie de générer un reçu pour un Paiement qui en a déjà un
// (la contrainte unique sur recus.paiement_id l'empêcherait aussi en base, mais on préfère
// la détecter nous-mêmes en amont pour renvoyer un message clair plutôt qu'une erreur SQL brute).
public class RecuDejaExistantException extends RuntimeException {
    public RecuDejaExistantException(Long paiementId) {
        super("Un reçu existe déjà pour le paiement d'id " + paiementId);
    }
}
