package gn.projet.cotisation.dto.paiements;

import gn.projet.cotisation.entity.Mois;

import java.util.Set;

public record CreatePaiementRequestDTO(
        Long utilisateurId,
        Set<Mois> mois,
        Integer annee,
        // Pour les mois payés en retard (mois déjà passé au moment du paiement) : le président choisit
        // ici s'il applique ou non la pénalité de retard habituelle. Sans effet sur les mois non en
        // retard, qui n'en génèrent jamais. null (absent du JSON) équivaut à true (comportement historique).
        Boolean appliquerPenaliteRetard
) {
    // Nom délibérément différent de l'accesseur généré (appliquerPenaliteRetard()) : un record ne
    // peut pas redéclarer cet accesseur avec un type de retour différent (boolean primitif au lieu
    // de Boolean). Le service appelle cette méthode plutôt que de relire le champ nullable lui-même.
    public boolean doitAppliquerPenaliteRetard() {
        return appliquerPenaliteRetard == null || appliquerPenaliteRetard;
    }
}
