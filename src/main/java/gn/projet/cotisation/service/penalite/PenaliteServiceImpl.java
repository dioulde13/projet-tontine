package gn.projet.cotisation.service.penalite;

import gn.projet.cotisation.dto.mapper.PenaliteMapper;
import gn.projet.cotisation.dto.penalites.PenaliteResponseDTO;
import gn.projet.cotisation.dto.penalites.UpdatePenaliteRequestDTO;
import gn.projet.cotisation.entity.Penalite;
import gn.projet.cotisation.entity.StatutPenalite;
import gn.projet.cotisation.entity.TypePenalite;
import gn.projet.cotisation.entity.Utilisateur;
import gn.projet.cotisation.exception.penalite.PenaliteNotFoundException;
import gn.projet.cotisation.exception.penalite.TypePenaliteManuelInvalideException;
import gn.projet.cotisation.exception.utilisateur.UtilisateurNotFoundException;
import gn.projet.cotisation.repository.penalite.PenaliteRepository;
import gn.projet.cotisation.repository.utilisateur.UtilisateurRepository;
import gn.projet.cotisation.service.village.VillageAccessService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PenaliteServiceImpl implements PenaliteService{


    private final PenaliteRepository penaliteRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final VillageAccessService villageAccessService;

    public PenaliteServiceImpl(
            PenaliteRepository penaliteRepository,
            UtilisateurRepository utilisateurRepository,
            VillageAccessService villageAccessService
    ) {
        this.penaliteRepository = penaliteRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.villageAccessService = villageAccessService;
    }

    @Override
    public PenaliteResponseDTO create(Long villageId, UpdatePenaliteRequestDTO dto , Utilisateur presidentConnecte) {
        villageAccessService.verifierEtRecupererVillageGere(villageId,presidentConnecte);

        // RETARD_PAIEMENT et ABSENCE_REUNION ne doivent être créées QUE par le déclenchement
        // automatique (PaiementServiceImpl), avec le montant lu dans ConfigurationVillage —
        // jamais saisies à la main, sinon on pourrait contourner la configuration du village.
        if (dto.type() == TypePenalite.RETARD_PAIEMENT || dto.type() == TypePenalite.ABSENCE_REUNION) {
            throw new TypePenaliteManuelInvalideException(dto.type().name());
        }

        Utilisateur membre = utilisateurRepository.findById(dto.utilisateurId())
                .filter(u -> u.getVillage() !=null && u.getVillage().getId().equals(villageId))
                .orElseThrow(() -> new UtilisateurNotFoundException(dto.utilisateurId()));

        Penalite penalite = new Penalite();
        penalite.setUtilisateur(membre);
        penalite.setType(dto.type());
        penalite.setMontant(dto.montant());
        penalite.setDate(dto.date());
        penalite.setStatut(dto.statut());

        // paiementOrigine / presenceOrigine restent null : cette pénalité est
       // créée manuellement,
        // pas déclenchée automatiquement par un retard de paiement ou une
       // absence.
                Penalite saved = penaliteRepository.save(penalite);
        return PenaliteMapper.toResponseDto(saved);
    }

    @Override
    public List<PenaliteResponseDTO> findAllPenalitesVillage(Long villageId, Utilisateur presidentConnecte) {
        villageAccessService.verifierEtRecupererVillageGere(villageId,
                presidentConnecte);

        return penaliteRepository.findByVillageId(villageId).stream()
                .map(PenaliteMapper::toResponseDto)
                .toList();
    }

    @Override
    public void delete(Long villageId, Long penaliteId, Utilisateur presidentConnecte) {
        villageAccessService.verifierEtRecupererVillageGere(villageId, presidentConnecte);

        Penalite penalite = penaliteRepository.findById(penaliteId)
                .filter(p -> p.getUtilisateur().getVillage() != null
                        && p.getUtilisateur().getVillage().getId().equals(villageId))
                .orElseThrow(() -> new PenaliteNotFoundException(penaliteId));

        penaliteRepository.delete(penalite);
    }

    @Override
    public PenaliteResponseDTO payer(Long villageId, Long penaliteId, Utilisateur presidentConnecte) {
        villageAccessService.verifierEtRecupererVillageGere(villageId, presidentConnecte);

        Penalite penalite = penaliteRepository.findById(penaliteId)
                .filter(p -> p.getUtilisateur().getVillage() != null
                        && p.getUtilisateur().getVillage().getId().equals(villageId))
                .orElseThrow(() -> new PenaliteNotFoundException(penaliteId));

        penalite.setStatut(StatutPenalite.PAYEE);
        Penalite saved = penaliteRepository.save(penalite);
        return PenaliteMapper.toResponseDto(saved);
    }
}
