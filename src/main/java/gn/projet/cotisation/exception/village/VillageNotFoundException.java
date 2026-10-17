package gn.projet.cotisation.exception.village;

public class VillageNotFoundException extends RuntimeException {

    public VillageNotFoundException(Long id) {
        super("Aucun village trouvé avec l'id " + id);
    }
}
