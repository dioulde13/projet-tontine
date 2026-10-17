package gn.projet.cotisation.dto.membre;

import java.math.BigDecimal;

// Un mois de l'année courante, entre janvier et le mois courant inclus, pour lequel le membre
// connecté n'a trouvé aucun Paiement correspondant : utilisé par /api/membres/moi/cotisations
// pour afficher "ce qu'il reste à payer".
public record MoisImpayeDTO(
        Integer mois,
        Integer annee,
        BigDecimal montant
) {
}
