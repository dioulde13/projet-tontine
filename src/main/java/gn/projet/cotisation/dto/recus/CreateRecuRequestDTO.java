package gn.projet.cotisation.dto.recus;

import java.util.List;

// Les ids des Paiements à "quittancer" ensemble dans un seul reçu (un seul suffit pour un reçu
// classique, plusieurs pour regrouper plusieurs mois du même membre en un seul reçu).
// Pas besoin d'envoyer utilisateurId/mois/annee/montant : on les retrouve via paiementIds
// (ils sont déjà enregistrés sur chaque Paiement au moment où il a été créé).
public record CreateRecuRequestDTO(
        List<Long> paiementIds
) {
}
