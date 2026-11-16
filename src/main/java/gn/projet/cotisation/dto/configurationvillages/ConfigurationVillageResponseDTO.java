package gn.projet.cotisation.dto.configurationvillages;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// DTO de RÉPONSE pour ConfigurationVillage. "villageId" référence le village par son id
// plutôt que d'imbriquer un VillageResponseDTO complet — cette config sera toujours consultée
// DANS le contexte d'un village déjà connu (ex: GET /api/villages/{id}/configuration).
public record ConfigurationVillageResponseDTO(
        Long id,
        Long villageId,
        BigDecimal montantCotisationMensuelle,
        BigDecimal montantPenaliteRetard,
        BigDecimal montantPenaliteAbsenceReunion,
        // Audit (voir BaseEntity) : la configuration est modifiable, donc la dernière modification
        // (updated*) est la plus utile à afficher — null si jamais modifiée hors contexte authentifié.
        LocalDateTime modifieLe,
        String modifiePar,
        // Nom complet résolu depuis modifiePar (prénom + nom de l'Utilisateur correspondant), pour
        // l'affichage. Null si modifiePar est null, OU si aucun utilisateur ne porte (plus) ce téléphone.
        String modifieParNom
) {
}
