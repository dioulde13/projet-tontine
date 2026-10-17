package gn.projet.cotisation.exception.village;

// Levée quand un PRESIDENT tente d'agir sur un village qui n'est PAS dans sa liste villagesGeres.
// Distincte de VillageNotFoundException (le village existe bien, c'est un problème de droits, pas d'existence)
// -> 403 Forbidden ("je sais qui tu es, mais ce n'est pas ton village"), pas 404.
public class VillageAccessDeniedException extends RuntimeException {

    // Prend le NOM du village (pas son id) : le message d'erreur doit être lisible pour un humain
    // (un président comprend "Tougue Centre" tout de suite, pas "l'id 1").
    public VillageAccessDeniedException(String villageNom) {
        super("Vous ne gérez pas le village " + villageNom);
    }
}
