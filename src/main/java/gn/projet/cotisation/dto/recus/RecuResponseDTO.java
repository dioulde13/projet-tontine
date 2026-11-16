package gn.projet.cotisation.dto.recus;

import gn.projet.cotisation.dto.paiements.PaiementResponseDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record RecuResponseDTO(
        Long id,
        String numero,
        LocalDate dateEmission,
        // Liste des paiements couverts par ce reçu (un reçu peut regrouper plusieurs mois réglés
        // d'un coup pour le même membre), chacun complet avec son Utilisateur imbriqué.
        List<PaiementResponseDTO> paiements,
        // Audit (voir BaseEntity) : un reçu n'est jamais modifié après génération, donc seul le
        // "créé par/le" a du sens ici — pas de updatedAt/updatedBy (toujours identiques, sans intérêt).
        LocalDateTime creeLe,
        // Téléphone du président qui a généré ce reçu ; null si générée hors contexte authentifié.
        String creePar,
        // Nom complet résolu depuis creePar (prénom + nom de l'Utilisateur correspondant), pour
        // l'affichage. Null si creePar est null, OU si aucun utilisateur ne porte (plus) ce téléphone
        // (ex: compte supprimé depuis) — jamais calculé pour une liste, seulement pour le détail d'UN
        // reçu (RecuServiceImpl.findOne), pour ne pas multiplier les requêtes sur une liste.
        String creeParNom
) {
}
