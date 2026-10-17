package gn.projet.cotisation.entity;

public enum Mois {

    JANVIER(1),
    FEVRIER(2),
    MARS(3),
    AVRIL(4),
    MAI(5),
    JUIN(6),
    JUILLET(7),
    AOUT(8),
    SEPTEMBRE(9),
    OCTOBRE(10),
    NOVEMBRE(11),
    DECEMBRE(12);

    private final int numero;

    Mois(int numero) {
        this.numero = numero;
    }

    public int getNumero() {
        return numero;
    }
}
