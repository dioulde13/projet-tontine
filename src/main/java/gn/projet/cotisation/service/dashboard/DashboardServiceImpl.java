package gn.projet.cotisation.service.dashboard;

import gn.projet.cotisation.dto.dashboard.AdminDashboardResponseDTO;
import gn.projet.cotisation.dto.dashboard.MembreDashboardResponseDTO;
import gn.projet.cotisation.dto.dashboard.PresidentDashboardResponseDTO;
import gn.projet.cotisation.dto.mapper.ReunionMapper;
import gn.projet.cotisation.dto.mapper.VillageMapper;
import gn.projet.cotisation.entity.ConfigurationVillage;
import gn.projet.cotisation.entity.Paiement;
import gn.projet.cotisation.entity.Penalite;
import gn.projet.cotisation.entity.Presence;
import gn.projet.cotisation.entity.Reunion;
import gn.projet.cotisation.entity.StatutPenalite;
import gn.projet.cotisation.entity.Utilisateur;
import gn.projet.cotisation.entity.Village;
import gn.projet.cotisation.exception.utilisateur.UtilisateurSansVillageException;
import gn.projet.cotisation.repository.configurationVillage.ConfigurationVillageRepository;
import gn.projet.cotisation.repository.paiement.PaiementRepository;
import gn.projet.cotisation.repository.penalite.PenaliteRepository;
import gn.projet.cotisation.repository.presence.PresenceRepository;
import gn.projet.cotisation.repository.reunion.ReunionRepository;
import gn.projet.cotisation.repository.utilisateur.UtilisateurRepository;
import gn.projet.cotisation.repository.village.VillageRepository;
import gn.projet.cotisation.service.village.VillageAccessService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final VillageRepository villageRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final PaiementRepository paiementRepository;
    private final PenaliteRepository penaliteRepository;
    private final ReunionRepository reunionRepository;
    private final PresenceRepository presenceRepository;
    private final ConfigurationVillageRepository configurationVillageRepository;
    private final VillageAccessService villageAccessService;

    public DashboardServiceImpl(
            VillageRepository villageRepository,
            UtilisateurRepository utilisateurRepository,
            PaiementRepository paiementRepository,
            PenaliteRepository penaliteRepository,
            ReunionRepository reunionRepository,
            PresenceRepository presenceRepository,
            ConfigurationVillageRepository configurationVillageRepository,
            VillageAccessService villageAccessService
    ) {
        this.villageRepository = villageRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.paiementRepository = paiementRepository;
        this.penaliteRepository = penaliteRepository;
        this.reunionRepository = reunionRepository;
        this.presenceRepository = presenceRepository;
        this.configurationVillageRepository = configurationVillageRepository;
        this.villageAccessService = villageAccessService;
    }

    @Override
    public AdminDashboardResponseDTO getAdminDashboard() {
        List<Utilisateur> tousLesUtilisateurs = utilisateurRepository.findAll();
        List<Paiement> tousLesPaiements = paiementRepository.findAll();
        List<Penalite> toutesLesPenalites = penaliteRepository.findAll();

        long nombrePenalitesNonPayees = toutesLesPenalites.stream()
                .filter(p -> p.getStatut() == StatutPenalite.NON_PAYEE)
                .count();

        BigDecimal montantTotalPenalitesNonPayees = toutesLesPenalites.stream()
                .filter(p -> p.getStatut() == StatutPenalite.NON_PAYEE)
                .map(Penalite::getMontant)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal montantTotalCotisationsCollectees = tousLesPaiements.stream()
                .map(Paiement::getMontant)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new AdminDashboardResponseDTO(
                villageRepository.count(),
                tousLesUtilisateurs.stream().filter(u -> aLeRole(u, "ADMIN")).count(),
                tousLesUtilisateurs.stream().filter(u -> aLeRole(u, "PRESIDENT")).count(),
                tousLesUtilisateurs.stream().filter(u -> aLeRole(u, "MEMBRE")).count(),
                reunionRepository.count(),
                tousLesPaiements.size(),
                montantTotalCotisationsCollectees,
                nombrePenalitesNonPayees,
                montantTotalPenalitesNonPayees
        );
    }

    @Override
    public PresidentDashboardResponseDTO getPresidentDashboard(Long villageId, Utilisateur presidentConnecte) {
        Village village = villageAccessService.verifierEtRecupererVillageGere(villageId, presidentConnecte);

        LocalDate aujourdHui = LocalDate.now();
        int moisCourant = aujourdHui.getMonthValue();
        int anneeCourante = aujourdHui.getYear();

        long nombreMembres = utilisateurRepository.findByVillageId(villageId).size();

        BigDecimal montantCotisationMensuelle = configurationVillageRepository.findByVillageId(villageId)
                .map(ConfigurationVillage::getMontantCotisationMensuelle)
                .orElse(null);

        List<Paiement> paiementsDuVillage = paiementRepository.findPaiementByVillage(villageId);

        List<Paiement> paiementsDuMoisCourant = paiementsDuVillage.stream()
                .filter(p -> p.getMois() == moisCourant && p.getAnnee() == anneeCourante)
                .toList();

        long nombreMembresAJourMoisCourant = paiementsDuMoisCourant.stream()
                .map(p -> p.getUtilisateur().getId())
                .distinct()
                .count();

        BigDecimal montantTotalCollecte = paiementsDuVillage.stream()
                .map(Paiement::getMontant)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal montantCollecteMoisCourant = paiementsDuMoisCourant.stream()
                .map(Paiement::getMontant)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<Penalite> penalitesDuVillage = penaliteRepository.findByVillageId(villageId);

        long nombrePenalitesNonPayees = penalitesDuVillage.stream()
                .filter(p -> p.getStatut() == StatutPenalite.NON_PAYEE)
                .count();

        BigDecimal montantTotalPenalitesNonPayees = penalitesDuVillage.stream()
                .filter(p -> p.getStatut() == StatutPenalite.NON_PAYEE)
                .map(Penalite::getMontant)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<Reunion> reunionsDuVillage = reunionRepository.findByVillageId(villageId);

        Reunion prochaineReunion = prochaineReunion(reunionsDuVillage);

        List<Presence> presencesDuVillage = presenceRepository.findByVillageId(villageId);
        double tauxPresence = tauxPresence(presencesDuVillage);

        return new PresidentDashboardResponseDTO(
                VillageMapper.toResponseDto(village),
                moisCourant,
                anneeCourante,
                nombreMembres,
                montantCotisationMensuelle,
                nombreMembresAJourMoisCourant,
                nombreMembres - nombreMembresAJourMoisCourant,
                montantTotalCollecte,
                montantCollecteMoisCourant,
                nombrePenalitesNonPayees,
                montantTotalPenalitesNonPayees,
                reunionsDuVillage.size(),
                prochaineReunion == null ? null : ReunionMapper.toResponseDto(prochaineReunion),
                tauxPresence
        );
    }

    @Override
    public MembreDashboardResponseDTO getMembreDashboard(Utilisateur membreConnecte) {
        Village village = membreConnecte.getVillage();
        if (village == null) {
            throw new UtilisateurSansVillageException(membreConnecte.getId());
        }

        LocalDate aujourdHui = LocalDate.now();
        int moisCourant = aujourdHui.getMonthValue();
        int anneeCourante = aujourdHui.getYear();

        BigDecimal montantCotisationMensuelle = configurationVillageRepository.findByVillageId(village.getId())
                .map(ConfigurationVillage::getMontantCotisationMensuelle)
                .orElse(null);

        List<Paiement> mesPaiements = paiementRepository.findByUtilisateurId(membreConnecte.getId());

        boolean aPayeMoisCourant = mesPaiements.stream()
                .anyMatch(p -> p.getMois() == moisCourant && p.getAnnee() == anneeCourante);

        BigDecimal montantTotalPaye = mesPaiements.stream()
                .map(Paiement::getMontant)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<Penalite> mesPenalites = penaliteRepository.findByUtilisateurId(membreConnecte.getId());

        long nombrePenalitesNonPayees = mesPenalites.stream()
                .filter(p -> p.getStatut() == StatutPenalite.NON_PAYEE)
                .count();

        BigDecimal montantTotalPenalitesNonPayees = mesPenalites.stream()
                .filter(p -> p.getStatut() == StatutPenalite.NON_PAYEE)
                .map(Penalite::getMontant)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Reunion prochaineReunion = prochaineReunion(reunionRepository.findByVillageId(village.getId()));

        List<Presence> mesPresences = presenceRepository.findByUtilisateurId(membreConnecte.getId());
        long nombrePresences = mesPresences.stream().filter(Presence::isPresent).count();
        long nombreAbsences = mesPresences.size() - nombrePresences;
        double tauxPresence = tauxPresence(mesPresences);

        return new MembreDashboardResponseDTO(
                VillageMapper.toResponseDto(village),
                moisCourant,
                anneeCourante,
                montantCotisationMensuelle,
                aPayeMoisCourant,
                montantTotalPaye,
                mesPaiements.size(),
                nombrePenalitesNonPayees,
                montantTotalPenalitesNonPayees,
                prochaineReunion == null ? null : ReunionMapper.toResponseDto(prochaineReunion),
                nombrePresences,
                nombreAbsences,
                tauxPresence
        );
    }

    private boolean aLeRole(Utilisateur utilisateur, String nomRole) {
        return utilisateur.getRoles().stream().anyMatch(r -> r.getNom().equals(nomRole));
    }

    // La prochaine réunion à venir (dateHeure >= maintenant), la plus proche en premier ; null si aucune.
    private Reunion prochaineReunion(List<Reunion> reunions) {
        LocalDateTime maintenant = LocalDateTime.now();
        return reunions.stream()
                .filter(r -> !r.getDateHeure().isBefore(maintenant))
                .min(Comparator.comparing(Reunion::getDateHeure))
                .orElse(null);
    }

    // Pourcentage (0 à 100) de présences marquées "present" parmi un ensemble de Presence ; 0 si vide,
    // pour ne jamais diviser par zéro.
    private double tauxPresence(List<Presence> presences) {
        if (presences.isEmpty()) {
            return 0d;
        }
        long nombrePresents = presences.stream().filter(Presence::isPresent).count();
        return (nombrePresents * 100d) / presences.size();
    }
}
