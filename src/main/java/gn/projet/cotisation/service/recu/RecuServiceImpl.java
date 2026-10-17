package gn.projet.cotisation.service.recu;

import gn.projet.cotisation.dto.mapper.RecuMapper;
import gn.projet.cotisation.dto.recus.CreateRecuRequestDTO;
import gn.projet.cotisation.dto.recus.RecuResponseDTO;
import gn.projet.cotisation.entity.Paiement;
import gn.projet.cotisation.entity.Recu;
import gn.projet.cotisation.entity.Utilisateur;
import gn.projet.cotisation.exception.paiement.PaiementNotFoundException;
import gn.projet.cotisation.exception.recu.PaiementsIncompatiblesException;
import gn.projet.cotisation.exception.recu.RecuDejaExistantException;
import gn.projet.cotisation.exception.recu.RecuNotFoundException;
import gn.projet.cotisation.repository.paiement.PaiementRepository;
import gn.projet.cotisation.repository.recu.RecuRepository;
import gn.projet.cotisation.service.village.VillageAccessService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class RecuServiceImpl implements RecuService {

    private final RecuRepository recuRepository;
    private final PaiementRepository paiementRepository;
    private final VillageAccessService villageAccessService;

    public RecuServiceImpl(
            RecuRepository recuRepository,
            PaiementRepository paiementRepository,
            VillageAccessService villageAccessService
    ) {
        this.recuRepository = recuRepository;
        this.paiementRepository = paiementRepository;
        this.villageAccessService = villageAccessService;
    }

    @Transactional
    @Override
    public RecuResponseDTO create(Long villageId, CreateRecuRequestDTO dto, Utilisateur presidentConnecte) {
        // Même garde qu'ailleurs : seul le président QUI GÈRE ce village peut générer un reçu dedans.
        villageAccessService.verifierEtRecupererVillageGere(villageId, presidentConnecte);

        List<Long> paiementIds = dto.paiementIds();
        if (paiementIds == null || paiementIds.isEmpty()) {
            throw new PaiementsIncompatiblesException("Sélectionnez au moins un paiement à quittancer.");
        }

        // On récupère chaque Paiement, en vérifiant au passage qu'il appartient bien à un membre
        // de CE village (sinon un président pourrait générer un reçu pour un paiement d'un autre village
        // juste en devinant son id), et qu'il n'a pas déjà son propre reçu.
        List<Paiement> paiements = paiementIds.stream()
                .map(id -> paiementRepository.findById(id)
                        .filter(p -> p.getUtilisateur().getVillage() != null
                                && p.getUtilisateur().getVillage().getId().equals(villageId))
                        .orElseThrow(() -> new PaiementNotFoundException(id)))
                .toList();

        for (Paiement p : paiements) {
            if (recuRepository.existsByPaiementId(p.getId())) {
                throw new RecuDejaExistantException(p.getId());
            }
        }

        // Un reçu quittance toujours UN SEUL membre : impossible de regrouper les paiements
        // de deux membres différents dans le même reçu.
        long nombreDeMembresDistincts = paiements.stream()
                .map(p -> p.getUtilisateur().getId())
                .distinct()
                .count();
        if (nombreDeMembresDistincts > 1) {
            throw new PaiementsIncompatiblesException(
                    "Impossible de regrouper dans un seul reçu des paiements de membres différents.");
        }

        Recu recu = new Recu();
        recu.setPaiements(paiements);
        recu.setDateEmission(LocalDate.now());
        // Numéro lisible et unique : "REC-<année d'émission>-<plus petit id de paiement du groupe>".
        // Un paiement ne pouvant jamais appartenir qu'à un seul reçu (contrainte unique sur la table
        // de jointure), le plus petit id du groupe ne sera plus jamais réutilisé par un autre reçu :
        // ça garantit l'unicité du numéro sans compteur séparé, même pour un reçu à plusieurs paiements.
        long idRepere = paiements.stream().mapToLong(Paiement::getId).min().orElseThrow();
        recu.setNumero("REC-" + LocalDate.now().getYear() + "-" + String.format("%05d", idRepere));

        Recu savedRecu = recuRepository.save(recu);
        return RecuMapper.toResponseDto(savedRecu);
    }

    @Override
    public List<RecuResponseDTO> findRecusByVillage(Long villageId, Utilisateur presidentConnecte) {
        villageAccessService.verifierEtRecupererVillageGere(villageId, presidentConnecte);
        return recuRepository.findRecuByVillage(villageId).stream()
                .map(RecuMapper::toResponseDto)
                .toList();
    }

    @Override
    public RecuResponseDTO findOne(Long villageId, Long recuId, Utilisateur presidentConnecte) {
        villageAccessService.verifierEtRecupererVillageGere(villageId, presidentConnecte);

        Recu recu = recuRepository.findById(recuId)
                .filter(r -> !r.getPaiements().isEmpty()
                        && r.getPaiements().get(0).getUtilisateur().getVillage() != null
                        && r.getPaiements().get(0).getUtilisateur().getVillage().getId().equals(villageId))
                .orElseThrow(() -> new RecuNotFoundException(recuId));

        return RecuMapper.toResponseDto(recu);
    }
}
