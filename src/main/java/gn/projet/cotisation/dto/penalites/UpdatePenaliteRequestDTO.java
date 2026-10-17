package gn.projet.cotisation.dto.penalites;

import gn.projet.cotisation.entity.StatutPenalite;
import gn.projet.cotisation.entity.TypePenalite;

import java.math.BigDecimal;
import java.time.LocalDate;

// Pas de paiementOrigineId / presenceOrigineId ici : ces liens sont posés automatiquement par le
// système quand une Penalite est déclenchée par un paiement en retard ou une absence — jamais saisis
// à la main par un client de l'API. Ce DTO reste utile pour une pénalité créée manuellement par le
// président (hors des deux déclencheurs automatiques) et pour changer un statut (ex: marquer PAYEE).
public record UpdatePenaliteRequestDTO(
        Long utilisateurId,
        TypePenalite type,
        BigDecimal montant,
        LocalDate date,
        StatutPenalite statut
) {
}
