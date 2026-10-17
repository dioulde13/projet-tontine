package gn.projet.cotisation.dto.recus;

import gn.projet.cotisation.dto.paiements.PaiementResponseDTO;

import java.time.LocalDate;
import java.util.List;

public record RecuResponseDTO(
        Long id,
        String numero,
        LocalDate dateEmission,
        // Liste des paiements couverts par ce reçu (un reçu peut regrouper plusieurs mois réglés
        // d'un coup pour le même membre), chacun complet avec son Utilisateur imbriqué.
        List<PaiementResponseDTO> paiements
) {
}
