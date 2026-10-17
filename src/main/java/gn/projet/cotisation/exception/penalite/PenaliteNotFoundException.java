package gn.projet.cotisation.exception.penalite;

public class PenaliteNotFoundException extends RuntimeException {

    public PenaliteNotFoundException(Long id) {
        super("Aucune pénalité trouvée avec l'id " + id);
    }
}
