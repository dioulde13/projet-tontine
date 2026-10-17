package gn.projet.cotisation.exception.penalite;

// Levée quand on tente de créer MANUELLEMENT une pénalité de type RETARD_PAIEMENT ou ABSENCE_REUNION —
// ces deux types ne doivent exister que via le déclenchement automatique (PaiementServiceImpl /
// un futur PresenceServiceImpl), jamais saisis directement par le président.
public class TypePenaliteManuelInvalideException extends RuntimeException {

    public TypePenaliteManuelInvalideException(String type) {
        super("Le type " + type + " ne peut pas être créé manuellement : il est réservé au "
                + "déclenchement automatique (paiement en retard ou absence à une réunion)");
    }
}
