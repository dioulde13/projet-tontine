package gn.projet.cotisation.service.dashboard;

import gn.projet.cotisation.dto.dashboard.AdminDashboardResponseDTO;
import gn.projet.cotisation.dto.dashboard.MembreDashboardResponseDTO;
import gn.projet.cotisation.dto.dashboard.PresidentDashboardResponseDTO;
import gn.projet.cotisation.dto.mapper.ReunionMapper;
import gn.projet.cotisation.dto.mapper.VillageMapper;
import gn.projet.cotisation.entity.ConfigurationVillage;
import gn.projet.cotisation.entity.Depense;
import gn.projet.cotisation.entity.Paiement;
import gn.projet.cotisation.entity.Penalite;
import gn.projet.cotisation.entity.Presence;
import gn.projet.cotisation.entity.Reunion;
import gn.projet.cotisation.entity.StatutPenalite;
import gn.projet.cotisation.entity.Utilisateur;
import gn.projet.cotisation.entity.Village;
import gn.projet.cotisation.exception.utilisateur.UtilisateurSansVillageException;
import gn.projet.cotisation.repository.configurationVillage.ConfigurationVillageRepository;
import gn.projet.cotisation.repository.depense.DepenseRepository;
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
    private final DepenseRepository depenseRepository;
    private final VillageAccessService villageAccessService;

    public DashboardServiceImpl(
            VillageRepository villageRepository,
            UtilisateurRepository utilisateurRepository,
            PaiementRepository paiementRepository,
            PenaliteRepository penaliteRepository,
            ReunionRepository reunionRepository,
            PresenceRepository presenceRepository,
            ConfigurationVillageRepository configurationVillageRepository,
            DepenseRepository depenseRepository,
            VillageAccessService villageAccessService
    ) {
        this.villageRepository = villageRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.paiementRepository = paiementRepository;
        this.penaliteRepository = penaliteRepository;
        this.reunionRepository = reunionRepository;
        this.presenceRepository = presenceRepository;
        this.configurationVillageRepository = configurationVillageRepository;
        this.depenseRepository = depenseRepository;
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
    public PresidentDashboardResponseDTO getPresidentDashboard(
            Long villageId, Utilisateur presidentConnecte, LocalDate dateDebut, LocalDate dateFin
    ) {
        Village village = villageAccessService.verifierEtRecupererVillageGere(villageId, presidentConnecte);

        LocalDate aujourdHui = LocalDate.now();
        int moisCourant = aujourdHui.getMonthValue();
        int anneeCourante = aujourdHui.getYear();

        long nombreMembres = utilisateurRepository.findByVillageId(villageId).size();

        BigDecimal montantCotisationMensuelle = configurationVillageRepository.findByVillageId(villageId)
                .map(ConfigurationVillage::getMontantCotisationMensuelle)
                .orElse(null);

        List<Paiement> paiementsDuVillage = paiementRepository.findPaiementByVillage(villageId);

        // "Membres à jour / cotisation du mois" restent toujours basés sur le mois CIVIL en cours,
        // filtre de période ou non : "être à jour ce mois-ci" n'a pas de sens sur une période arbitraire.
        List<Paiement> paiementsDuMoisCourant = paiementsDuVillage.stream()
                .filter(p -> p.getMois() == moisCourant && p.getAnnee() == anneeCourante)
                .toList();

        long nombreMembresAJourMoisCourant = paiementsDuMoisCourant.stream()
                .map(p -> p.getUtilisateur().getId())
                .distinct()
                .count();

        BigDecimal montantCollecteMoisCourant = paiementsDuMoisCourant.stream()
                .map(Paiement::getMontant)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Agrégats historiques (collecté, pénalités, réunions, taux de présence) : restreints à
        // [dateDebut, dateFin] quand fournies (bornes incluses), "depuis toujours" sinon — même
        // comportement qu'avant l'ajout du filtre quand aucune borne n'est passée.
        List<Paiement> paiementsPeriode = paiementsDuVillage.stream()
                .filter(p -> dansPeriode(p.getDatePaiement(), dateDebut, dateFin))
                .toList();

        BigDecimal montantTotalCollecte = paiementsPeriode.stream()
                .map(Paiement::getMontant)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<Penalite> penalitesPeriode = penaliteRepository.findByVillageId(villageId).stream()
                .filter(p -> dansPeriode(p.getDate(), dateDebut, dateFin))
                .toList();

        long nombrePenalitesNonPayees = penalitesPeriode.stream()
                .filter(p -> p.getStatut() == StatutPenalite.NON_PAYEE)
                .count();

        BigDecimal montantTotalPenalitesNonPayees = penalitesPeriode.stream()
                .filter(p -> p.getStatut() == StatutPenalite.NON_PAYEE)
                .map(Penalite::getMontant)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal montantTotalPenalites = penalitesPeriode.stream()
                .map(Penalite::getMontant)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal montantTotalPenalitesPayees = montantTotalPenalites.subtract(montantTotalPenalitesNonPayees);

        List<Depense> depensesPeriode = depenseRepository.findByVillageId(villageId).stream()
                .filter(d -> dansPeriode(d.getDate(), dateDebut, dateFin))
                .toList();

        BigDecimal montantTotalDepenses = depensesPeriode.stream()
                .map(Depense::getMontant)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal montantCaisse = montantTotalCollecte.add(montantTotalPenalitesPayees).subtract(montantTotalDepenses);

        List<Reunion> reunionsDuVillage = reunionRepository.findByVillageId(villageId);
        // La prochaine réunion reste celle à venir à partir de maintenant, jamais affectée par le filtre.
        Reunion prochaineReunion = prochaineReunion(reunionsDuVillage);

        List<Reunion> reunionsPeriode = reunionsDuVillage.stream()
                .filter(r -> dansPeriode(r.getDateHeure().toLocalDate(), dateDebut, dateFin))
                .toList();

        List<Presence> presencesPeriode = presenceRepository.findByVillageId(villageId).stream()
                .filter(p -> dansPeriode(p.getReunion().getDateHeure().toLocalDate(), dateDebut, dateFin))
                .toList();
        double tauxPresence = tauxPresence(presencesPeriode);

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
                montantTotalPenalites,
                montantTotalDepenses,
                montantCaisse,
                reunionsPeriode.size(),
                prochaineReunion == null ? null : ReunionMapper.toResponseDto(prochaineReunion),
                tauxPresence
        );
    }

    // true si `date` tombe dans [debut, fin] (bornes incluses, chacune optionnelle = illimitée).
    private boolean dansPeriode(LocalDate date, LocalDate debut, LocalDate fin) {
        if (debut != null && date.isBefore(debut)) {
            return false;
        }
        return fin == null || !date.isAfter(fin);
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
