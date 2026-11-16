package gn.projet.cotisation.dto.penalites;

import gn.projet.cotisation.entity.StatutPenalite;
import gn.projet.cotisation.entity.TypePenalite;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDate;

// Pas de paiementOrigineId / presenceOrigineId ici : ces liens sont posés automatiquement par le
// système quand une Penalite est déclenchée par un paiement en retard ou une absence — jamais saisis
// à la main par un client de l'API. Ce DTO reste utile pour une pénalité créée manuellement par le
// président (hors des deux déclencheurs automatiques) et pour changer un statut (ex: marquer PAYEE).
public record UpdatePenaliteRequestDTO(
        @NotNull(message = "Le membre est requis")
        Long utilisateurId,
        @NotNull(message = "Le type est requis")
        TypePenalite type,
        @NotNull(message = "Le montant est requis")
        @PositiveOrZero(message = "Le montant ne peut pas être négatif")
        BigDecimal montant,
        @NotNull(message = "La date est requise")
        LocalDate date,
        @NotNull(message = "Le statut est requis")
        StatutPenalite statut
) {
}
