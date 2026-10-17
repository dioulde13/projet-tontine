package gn.projet.cotisation.exception.utilisateur;

// Levée quand un utilisateur sans rôle MEMBRE (ou MEMBRE mal configuré, sans village assigné)
// appelle le tableau de bord "membre" : ce endpoint a besoin d'un village d'appartenance pour
// calculer quoi que ce soit (cotisation attendue, prochaine réunion du village...).
public class UtilisateurSansVillageException extends RuntimeException {

    public UtilisateurSansVillageException(Long utilisateurId) {
        super("L'utilisateur " + utilisateurId + " n'est associé à aucun village");
    }
}
