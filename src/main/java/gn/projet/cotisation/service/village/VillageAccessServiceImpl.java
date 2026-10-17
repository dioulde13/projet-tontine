package gn.projet.cotisation.service.village;

import gn.projet.cotisation.entity.Utilisateur;
import gn.projet.cotisation.entity.Village;
import gn.projet.cotisation.exception.village.VillageAccessDeniedException;
import gn.projet.cotisation.exception.village.VillageNotFoundException;
import gn.projet.cotisation.repository.village.VillageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class VillageAccessServiceImpl implements VillageAccessService {

    private final VillageRepository villageRepository;

    public VillageAccessServiceImpl(VillageRepository villageRepository) {
        this.villageRepository = villageRepository;
    }

    @Override
    public Village verifierEtRecupererVillageGere(Long villageId, Utilisateur president) {
        // "villageId" vient tel quel du paramètre reçu par cette méthode — remonte jusqu'à
        // UtilisateurServiceImpl.createMembre(), qui l'a lui-même reçu du contrôleur (donc de l'URL).
        // "president" vient de la même chaîne : CustomUserDetails.getUtilisateur() -> contrôleur -> service -> ici.
        log.debug("Vérification d'accès : villageId={}, president.telephone={}, president.villagesGeres.size={}",
                villageId, president.getTelephone(), president.getVillagesGeres().size());

        Village village = villageRepository.findById(villageId)
                .orElseThrow(() -> new VillageNotFoundException(villageId));

        // au moins un village de villagesGeres doit porter cet id
        boolean gereParCePresident = president.getVillagesGeres().stream()
                .anyMatch(v -> v.getId().equals(villageId));

        log.debug("Résultat : village={} (id={}), géré par ce président ? {}",
                village.getNom(), village.getId(), gereParCePresident);

        if (!gereParCePresident) {
            // On a déjà chargé "village" juste au-dessus : on réutilise son nom directement,
            // pas besoin d'une requête supplémentaire.
            throw new VillageAccessDeniedException(village.getNom());
        }

        return village;
    }
}
