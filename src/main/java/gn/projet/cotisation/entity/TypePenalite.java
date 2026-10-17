package gn.projet.cotisation.entity;

// Enum = un ensemble fermé de valeurs possibles, plus sûr qu'une simple String
// (impossible d'écrire une valeur qui n'existe pas, contrairement à un champ texte libre).
public enum TypePenalite {
    RETARD_PAIEMENT,   // déclenchée automatiquement quand un Paiement est enregistré pour un mois déjà passé
    ABSENCE_REUNION,   // déclenchée automatiquement quand une Presence est enregistrée avec present = false
    AUTRE              // seul type autorisé en création MANUELLE (motif propre au président, montant libre)
}
