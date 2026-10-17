package gn.projet.cotisation.exception.configurationvillage;

public class ConfigurationVillageNotFoundException extends RuntimeException {
    public ConfigurationVillageNotFoundException(Long villageId) {
        super("Aucune configuration trouvée pour le village avec l'id " + villageId);
    }
}
