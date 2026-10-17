package gn.projet.cotisation.service.village;

import gn.projet.cotisation.entity.Utilisateur;
import gn.projet.cotisation.entity.Village;

// Centralise la vérification "ce village existe-t-il, et le président donné le gère-t-il ?" —
// destinée à être réutilisée par TOUTE future fonctionnalité scopée par village (membres, paiements,
// réunions, présences, pénalités...), pour ne pas dupliquer cette logique dans chaque service.
public interface VillageAccessService {

    // Lève VillageNotFoundException si villageId n'existe pas, VillageAccessDeniedException si "president"
    // ne gère pas ce village précis. Renvoie l'entité Village (déjà chargée) si tout est en ordre —
    // évite à l'appelant d'avoir à refaire une requête pour l'obtenir.
    Village verifierEtRecupererVillageGere(Long villageId, Utilisateur president);
}
