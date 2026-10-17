package gn.projet.cotisation.exception.paiement;

// Levée quand on tente d'enregistrer un paiement pour un (membre, mois, année) déjà payé — la table
// paiements porte une contrainte unique sur ces 3 colonnes (voir l'entité Paiement). Vérifiée
// explicitement AVANT l'insertion pour donner un message clair plutôt que de laisser remonter une
// DataIntegrityViolationException générique (gérée par GlobalExceptionHandler, mais peu parlante).
public class PaiementDejaExistantException extends RuntimeException {
    public PaiementDejaExistantException(String nomComplet, int mois, int annee) {
        super(nomComplet + " a déjà un paiement enregistré pour " + mois + "/" + annee);
    }
}
