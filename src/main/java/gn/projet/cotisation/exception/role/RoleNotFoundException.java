package gn.projet.cotisation.exception.role;

// RuntimeException = exception "non vérifiée" (unchecked) : pas besoin d'écrire "throws RoleNotFoundException"
// dans la signature des méthodes qui la lèvent, ni de la capturer obligatoirement avec try/catch.
// C'est le choix standard pour des erreurs métier en Spring : elles remontent naturellement jusqu'au
// GlobalExceptionHandler sans polluer chaque signature de méthode sur le chemin.
public class RoleNotFoundException extends RuntimeException {

    public RoleNotFoundException(Long id) {
        // le message passé au constructeur parent devient ex.getMessage(), utilisé par le handler global
        super("Aucun rôle trouvé avec l'id " + id);
    }

    // Second constructeur : utilisé quand on cherche un rôle par son NOM plutôt que son id
    // (ex: RoleRepository.findByNom("MEMBRE") lors de la création automatique d'un membre) —
    // ce cas signale en réalité une erreur de configuration (le rôle MEMBRE devrait toujours exister).
    public RoleNotFoundException(String nom) {
        super("Aucun rôle trouvé avec le nom " + nom);
    }
}
