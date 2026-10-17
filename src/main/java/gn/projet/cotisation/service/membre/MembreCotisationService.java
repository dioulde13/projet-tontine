package gn.projet.cotisation.service.membre;

import gn.projet.cotisation.dto.membre.MembreCotisationsResponseDTO;
import gn.projet.cotisation.entity.Utilisateur;

public interface MembreCotisationService {

    // Vue personnelle du membre connecté, déduite de son propre village (pas de villageId dans l'URL,
    // même principe que DashboardService.getMembreDashboard). anneeDemandee : année sur laquelle
    // calculer les mois impayés ; null = année courante (comportement par défaut).
    MembreCotisationsResponseDTO getMesCotisations(Utilisateur membreConnecte, Integer anneeDemandee);
}
