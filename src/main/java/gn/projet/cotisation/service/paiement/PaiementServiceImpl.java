package gn.projet.cotisation.service.paiement;

import gn.projet.cotisation.dto.mapper.PaiementMapper;
import gn.projet.cotisation.dto.paiements.CreatePaiementRequestDTO;
import gn.projet.cotisation.dto.paiements.PaiementResponseDTO;
import gn.projet.cotisation.entity.*;
import gn.projet.cotisation.exception.configurationvillage.ConfigurationVillageNotFoundException;
import gn.projet.cotisation.exception.paiement.PaiementDejaExistantException;
import gn.projet.cotisation.exception.paiement.PaiementNotFoundException;
import gn.projet.cotisation.exception.utilisateur.UtilisateurNotFoundException;
import gn.projet.cotisation.repository.configurationVillage.ConfigurationVillageRepository;
import gn.projet.cotisation.repository.paiement.PaiementRepository;
import gn.projet.cotisation.repository.penalite.PenaliteRepository;
import gn.projet.cotisation.repository.utilisateur.UtilisateurRepository;
import gn.projet.cotisation.service.village.VillageAccessService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Service
public class PaiementServiceImpl  implements PaiementServices{
    private final PaiementRepository paiementRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final VillageAccessService villageAccessService;
    private final ConfigurationVillageRepository configurationVillageRepository; // lire lesmontants
    private final PenaliteRepository penaliteRepository; // créer la pénalité de retard sibesoin

    public PaiementServiceImpl(
            PaiementRepository paiementRepository,
            UtilisateurRepository utilisateurRepository,
            VillageAccessService villageAccessService,
            ConfigurationVillageRepository configurationVillageRepository,
            PenaliteRepository penaliteRepository
    ) {
        this.paiementRepository = paiementRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.villageAccessService = villageAccessService;
        this.configurationVillageRepository = configurationVillageRepository;
        this.penaliteRepository = penaliteRepository;
    }


    @Transactional
    @Override
    public List<PaiementResponseDTO> create(Long villageId,
                                            CreatePaiementRequestDTO dto, Utilisateur presidentConnecte) {
        villageAccessService.verifierEtRecupererVillageGere(villageId,
                presidentConnecte);

        ConfigurationVillage config =
                configurationVillageRepository.findByVillageId(villageId)
                        .orElseThrow(() -> new
                                ConfigurationVillageNotFoundException(villageId));

        Utilisateur membre =
                utilisateurRepository.findById(dto.utilisateurId())
                        .filter(u -> u.getVillage() != null &&
                                u.getVillage().getId().equals(villageId))
                        .orElseThrow(() -> new
                                UtilisateurNotFoundException(dto.utilisateurId()));

        LocalDate aujourdHui = LocalDate.now();

        List<PaiementResponseDTO> resultats = new ArrayList<>();

        // Un Paiement par mois choisi, évalué indépendamment pour le
        //retard
        for (Mois moisChoisi : dto.mois()) {
            int mois = moisChoisi.getNumero(); // JANVIER -> 1, FEVRIER ->
            //2, etc.

            // Vérifié explicitement avant l'insertion (plutôt que de laisser la contrainte unique de
            // la base le faire) : donne un message clair nommant le membre et la période, au lieu
            // d'un conflit générique ("valeur en doublon ?") peu compréhensible pour le président.
            if (paiementRepository.existsByUtilisateur_IdAndMoisAndAnnee(membre.getId(), mois, dto.annee())) {
                throw new PaiementDejaExistantException(membre.getPrenom() + " " + membre.getNom(), mois, dto.annee());
            }

            Paiement paiement = new Paiement();
            paiement.setUtilisateur(membre);
            paiement.setMois(mois);
            paiement.setAnnee(dto.annee());
            paiement.setMontant(config.getMontantCotisationMensuelle());
            paiement.setDatePaiement(aujourdHui);
            Paiement savedPaiement = paiementRepository.save(paiement);
            YearMonth moisPaye = YearMonth.of(dto.annee(), mois);
            boolean enRetard = moisPaye.isBefore(YearMonth.now());
            if (enRetard && dto.doitAppliquerPenaliteRetard()) {
                Penalite penalite = new Penalite();
                penalite.setUtilisateur(membre);
                penalite.setType(TypePenalite.RETARD_PAIEMENT);
                penalite.setMontant(config.getMontantPenaliteRetard());
                penalite.setDate(aujourdHui);
                penalite.setStatut(StatutPenalite.NON_PAYEE);
                penalite.setPaiementOrigine(savedPaiement);
                penaliteRepository.save(penalite);
            }
            resultats.add(PaiementMapper.toResponseDto(savedPaiement));
        }

        return resultats;
    }



    @Override
    public List<PaiementResponseDTO> findPaiementByVillage(Long villageId,
                                                           Utilisateur presidentConnecte) {
        villageAccessService.verifierEtRecupererVillageGere(villageId,
                presidentConnecte);
        return
                paiementRepository.findPaiementByVillage(villageId).stream()
                        .map(PaiementMapper::toResponseDto)
                        .toList();
    }

    @Override
    public void delete(Long villageId, Long paiementId, Utilisateur
            presidentConnecte) {
        villageAccessService.verifierEtRecupererVillageGere(villageId,
                presidentConnecte);

        Paiement paiement = paiementRepository.findById(paiementId)
                .filter(p -> p.getUtilisateur().getVillage() != null
                        &&
                        p.getUtilisateur().getVillage().getId().equals(villageId))
                .orElseThrow(() -> new
                        PaiementNotFoundException(paiementId));

        paiementRepository.delete(paiement);
    }
}
