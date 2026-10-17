package gn.projet.cotisation.dto.paiements;

import gn.projet.cotisation.dto.utilisateurs.UtilisateurResponseDTO;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PaiementResponseDTO(
        Long id,
        UtilisateurResponseDTO utilisateur, // le membre concerné, référencé par id
        Integer mois,
        Integer annee,
        BigDecimal montant,
        LocalDate datePaiement
) {
}
