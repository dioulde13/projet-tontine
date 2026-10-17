package gn.projet.cotisation.dto.membre;

import gn.projet.cotisation.dto.paiements.PaiementResponseDTO;
import gn.projet.cotisation.dto.penalites.PenaliteResponseDTO;

import java.math.BigDecimal;
import java.util.List;

// Réponse de GET /api/membres/moi/cotisations : la vue détaillée "ce que j'ai payé / ce qu'il me
// reste à payer / mes pénalités" du membre connecté (complète le résumé chiffré déjà exposé par
// MembreDashboardResponseDTO avec les listes complètes et le détail des mois encore dus).
public record MembreCotisationsResponseDTO(
        // null si ConfigurationVillage n'a pas encore été créée pour le village du membre.
        BigDecimal montantCotisationMensuelle,
        // Historique complet des paiements du membre, du plus récent au plus ancien.
        List<PaiementResponseDTO> paiements,
        BigDecimal montantTotalPaye,
        // Mois de l'année courante (janvier -> mois courant inclus) sans Paiement correspondant.
        List<MoisImpayeDTO> moisImpayes,
        BigDecimal montantTotalDu,
        // Historique complet des pénalités du membre, de la plus récente à la plus ancienne.
        List<PenaliteResponseDTO> penalites,
        BigDecimal montantTotalPenalitesNonPayees
) {
}
