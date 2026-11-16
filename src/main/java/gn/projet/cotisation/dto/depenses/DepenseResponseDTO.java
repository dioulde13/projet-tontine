package gn.projet.cotisation.dto.depenses;

import gn.projet.cotisation.dto.typemouvementcaisse.TypeMouvementCaisseResponseDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record DepenseResponseDTO(
        Long id,
        TypeMouvementCaisseResponseDTO type,
        BigDecimal montant,
        String motif,
        LocalDate date,
        String nomComptable,
        String numeroRecu,
        LocalDate dateEmissionRecu,
        // Audit (voir BaseEntity) : une dépense n'est jamais modifiée après création, donc seul le
        // "créé par/le" a du sens ici — même raisonnement que RecuResponseDTO.
        LocalDateTime creeLe,
        String creePar,
        // Nom complet résolu depuis creePar, uniquement sur le détail (voir DepenseServiceImpl.findOne),
        // jamais sur la liste (pour ne pas multiplier les requêtes).
        String creeParNom
) {
}
