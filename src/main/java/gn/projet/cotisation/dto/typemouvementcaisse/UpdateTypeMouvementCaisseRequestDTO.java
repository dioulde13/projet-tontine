package gn.projet.cotisation.dto.typemouvementcaisse;

import jakarta.validation.constraints.NotBlank;

public record UpdateTypeMouvementCaisseRequestDTO(
        @NotBlank(message = "Le libellé est requis")
        String libelle
) {
}
