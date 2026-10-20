package gn.projet.cotisation.service.dashboard;

import gn.projet.cotisation.dto.dashboard.AdminDashboardResponseDTO;
import gn.projet.cotisation.dto.dashboard.MembreDashboardResponseDTO;
import gn.projet.cotisation.dto.dashboard.PresidentDashboardResponseDTO;
import gn.projet.cotisation.entity.ConfigurationVillage;
import gn.projet.cotisation.entity.Paiement;
import gn.projet.cotisation.entity.Penalite;
import gn.projet.cotisation.entity.Presence;
import gn.projet.cotisation.entity.Reunion;
import gn.projet.cotisation.entity.Role;
import gn.projet.cotisation.entity.StatutPenalite;
import gn.projet.cotisation.entity.TypePenalite;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardServiceImplTest {

    @Mock
    private VillageRepository villageRepository;
    @Mock
    private UtilisateurRepository utilisateurRepository;
    @Mock
    private PaiementRepository paiementRepository;
    @Mock
    private PenaliteRepository penaliteRepository;
    @Mock
    private ReunionRepository reunionRepository;
    @Mock
    private PresenceRepository presenceRepository;
    @Mock
    private ConfigurationVillageRepository configurationVillageRepository;
    @Mock
    private VillageAccessService villageAccessService;

    @InjectMocks
    private DashboardServiceImpl dashboardService;

    private Village village;

    @BeforeEach
    void setUp() {
        village = new Village(1L, "Kindia");
    }

    private Role role(String nom) {
        Role role = new Role();
        role.setNom(nom);
        return role;
    }

    private Utilisateur utilisateur(Long id, String nomRole) {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId(id);
        utilisateur.setNom("Nom" + id);
        utilisateur.setPrenom("Prenom" + id);
        utilisateur.setTelephone("60000" + id);
        utilisateur.setMotDePasse("hash");
        utilisateur.getRoles().add(role(nomRole));
        return utilisateur;
    }

    private Paiement paiement(Utilisateur utilisateur, int mois, int annee, String montant) {
        Paiement paiement = new Paiement();
        paiement.setUtilisateur(utilisateur);
        paiement.setMois(mois);
        paiement.setAnnee(annee);
        paiement.setMontant(new BigDecimal(montant));
        paiement.setDatePaiement(LocalDate.now());
        return paiement;
    }

    private Penalite penalite(Utilisateur utilisateur, StatutPenalite statut, String montant) {
        Penalite penalite = new Penalite();
        penalite.setUtilisateur(utilisateur);
        penalite.setType(TypePenalite.RETARD_PAIEMENT);
        penalite.setMontant(new BigDecimal(montant));
        penalite.setDate(LocalDate.now());
        penalite.setStatut(statut);
        return penalite;
    }

    private Presence presence(Utilisateur utilisateur, boolean present) {
        Presence presence = new Presence();
        presence.setUtilisateur(utilisateur);
        presence.setPresent(present);
        return presence;
    }

    // getPresidentDashboard lit désormais p.getReunion().getDateHeure() (filtre de période) : cette
    // variante attache une Reunion, contrairement à presence(utilisateur, present) ci-dessus (utilisée
    // par getMembreDashboard, qui ne regarde jamais la réunion d'une présence).
    private Presence presence(Utilisateur utilisateur, boolean present, Reunion reunion) {
        Presence presence = presence(utilisateur, present);
        presence.setReunion(reunion);
        return presence;
    }

    // ---------------------------------------------------------------------
    // getAdminDashboard
    // ---------------------------------------------------------------------

    @Test
    void getAdminDashboard_calculeLesCompteursEtMontantsSurToutesLesDonnees() {
        Utilisateur admin = utilisateur(1L, "ADMIN");
        Utilisateur president1 = utilisateur(2L, "PRESIDENT");
        Utilisateur president2 = utilisateur(3L, "PRESIDENT");
        Utilisateur membre1 = utilisateur(4L, "MEMBRE");
        Utilisateur membre2 = utilisateur(5L, "MEMBRE");

        when(utilisateurRepository.findAll())
                .thenReturn(List.of(admin, president1, president2, membre1, membre2));
        when(villageRepository.count()).thenReturn(3L);
        when(reunionRepository.count()).thenReturn(7L);
        when(paiementRepository.findAll()).thenReturn(List.of(
                paiement(membre1, 1, 2026, "50000"),
                paiement(membre2, 1, 2026, "30000")
        ));
        when(penaliteRepository.findAll()).thenReturn(List.of(
                penalite(membre1, StatutPenalite.NON_PAYEE, "5000"),
                penalite(membre2, StatutPenalite.PAYEE, "2000"),
                penalite(membre2, StatutPenalite.NON_PAYEE, "1000")
        ));

        AdminDashboardResponseDTO dto = dashboardService.getAdminDashboard();

        assertThat(dto.nombreVillages()).isEqualTo(3L);
        assertThat(dto.nombreAdmins()).isEqualTo(1L);
        assertThat(dto.nombrePresidents()).isEqualTo(2L);
        assertThat(dto.nombreMembres()).isEqualTo(2L);
        assertThat(dto.nombreReunions()).isEqualTo(7L);
        assertThat(dto.nombrePaiements()).isEqualTo(2);
        assertThat(dto.montantTotalCotisationsCollectees()).isEqualByComparingTo("80000");
        assertThat(dto.nombrePenalitesNonPayees()).isEqualTo(2L);
        assertThat(dto.montantTotalPenalitesNonPayees()).isEqualByComparingTo("6000");
    }

    @Test
    void getAdminDashboard_renvoieDesZerosQuandAucuneDonnee() {
        when(utilisateurRepository.findAll()).thenReturn(List.of());
        when(villageRepository.count()).thenReturn(0L);
        when(reunionRepository.count()).thenReturn(0L);
        when(paiementRepository.findAll()).thenReturn(List.of());
        when(penaliteRepository.findAll()).thenReturn(List.of());

        AdminDashboardResponseDTO dto = dashboardService.getAdminDashboard();

        assertThat(dto.nombreVillages()).isZero();
        assertThat(dto.nombreAdmins()).isZero();
        assertThat(dto.nombrePaiements()).isZero();
        assertThat(dto.montantTotalCotisationsCollectees()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(dto.nombrePenalitesNonPayees()).isZero();
        assertThat(dto.montantTotalPenalitesNonPayees()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    // ---------------------------------------------------------------------
    // getPresidentDashboard
    // ---------------------------------------------------------------------

    @Test
    void getPresidentDashboard_calculeLaVueDuVillageGereParLePresident() {
        Utilisateur president = utilisateur(1L, "PRESIDENT");
        Utilisateur membreAJour = utilisateur(2L, "MEMBRE");
        Utilisateur membreEnRetard = utilisateur(3L, "MEMBRE");

        LocalDate aujourdHui = LocalDate.now();
        int moisCourant = aujourdHui.getMonthValue();
        int anneeCourante = aujourdHui.getYear();

        when(villageAccessService.verifierEtRecupererVillageGere(1L, president)).thenReturn(village);
        when(utilisateurRepository.findByVillageId(1L)).thenReturn(List.of(membreAJour, membreEnRetard));

        ConfigurationVillage config = new ConfigurationVillage();
        config.setVillage(village);
        config.setMontantCotisationMensuelle(new BigDecimal("10000"));
        config.setMontantPenaliteRetard(new BigDecimal("1000"));
        config.setMontantPenaliteAbsenceReunion(new BigDecimal("500"));
        when(configurationVillageRepository.findByVillageId(1L)).thenReturn(java.util.Optional.of(config));

        Paiement paiementMoisCourant = paiement(membreAJour, moisCourant, anneeCourante, "10000");
        Paiement paiementMoisPasse = paiement(membreEnRetard, moisCourant == 1 ? 12 : moisCourant - 1,
                moisCourant == 1 ? anneeCourante - 1 : anneeCourante, "10000");
        when(paiementRepository.findPaiementByVillage(1L))
                .thenReturn(List.of(paiementMoisCourant, paiementMoisPasse));

        when(penaliteRepository.findByVillageId(1L)).thenReturn(List.of(
                penalite(membreEnRetard, StatutPenalite.NON_PAYEE, "1000")
        ));

        Reunion reunionPassee = new Reunion();
        reunionPassee.setVillage(village);
        reunionPassee.setDateHeure(LocalDateTime.now().minusDays(5));
        reunionPassee.setLieu("Chez le president");

        Reunion reunionFuture = new Reunion();
        reunionFuture.setVillage(village);
        reunionFuture.setDateHeure(LocalDateTime.now().plusDays(3));
        reunionFuture.setLieu("Ecole du village");

        when(reunionRepository.findByVillageId(1L)).thenReturn(List.of(reunionPassee, reunionFuture));

        when(presenceRepository.findByVillageId(1L)).thenReturn(List.of(
                presence(membreAJour, true, reunionPassee),
                presence(membreEnRetard, false, reunionPassee)
        ));

        PresidentDashboardResponseDTO dto = dashboardService.getPresidentDashboard(1L, president, null, null);

        assertThat(dto.village().id()).isEqualTo(1L);
        assertThat(dto.moisCourant()).isEqualTo(moisCourant);
        assertThat(dto.anneeCourante()).isEqualTo(anneeCourante);
        assertThat(dto.nombreMembres()).isEqualTo(2L);
        assertThat(dto.montantCotisationMensuelle()).isEqualByComparingTo("10000");
        assertThat(dto.nombreMembresAJourMoisCourant()).isEqualTo(1L);
        assertThat(dto.nombreMembresEnRetardMoisCourant()).isEqualTo(1L);
        assertThat(dto.montantTotalCollecte()).isEqualByComparingTo("20000");
        assertThat(dto.montantCollecteMoisCourant()).isEqualByComparingTo("10000");
        assertThat(dto.nombrePenalitesNonPayees()).isEqualTo(1L);
        assertThat(dto.montantTotalPenalitesNonPayees()).isEqualByComparingTo("1000");
        assertThat(dto.montantTotalPenalites()).isEqualByComparingTo("1000");
        assertThat(dto.montantCaisse()).isEqualByComparingTo("20000"); // pénalité non payée : pas encore en caisse
        assertThat(dto.nombreReunions()).isEqualTo(2);
        assertThat(dto.prochaineReunion()).isNotNull();
        assertThat(dto.prochaineReunion().lieu()).isEqualTo("Ecole du village");
        assertThat(dto.tauxPresence()).isEqualTo(50.0d);
    }

    @Test
    void getPresidentDashboard_restreintLesAgregatsHistoriquesALaPeriodeDemandee() {
        Utilisateur president = utilisateur(1L, "PRESIDENT");
        Utilisateur membre = utilisateur(2L, "MEMBRE");

        when(villageAccessService.verifierEtRecupererVillageGere(1L, president)).thenReturn(village);
        when(utilisateurRepository.findByVillageId(1L)).thenReturn(List.of(membre));
        when(configurationVillageRepository.findByVillageId(1L)).thenReturn(java.util.Optional.empty());

        LocalDate dansLaPeriode = LocalDate.of(2026, 3, 15);
        LocalDate horsPeriode = LocalDate.of(2026, 5, 1);

        Paiement paiementDansLaPeriode = new Paiement();
        paiementDansLaPeriode.setUtilisateur(membre);
        paiementDansLaPeriode.setMois(3);
        paiementDansLaPeriode.setAnnee(2026);
        paiementDansLaPeriode.setMontant(new BigDecimal("10000"));
        paiementDansLaPeriode.setDatePaiement(dansLaPeriode);

        Paiement paiementHorsPeriode = new Paiement();
        paiementHorsPeriode.setUtilisateur(membre);
        paiementHorsPeriode.setMois(5);
        paiementHorsPeriode.setAnnee(2026);
        paiementHorsPeriode.setMontant(new BigDecimal("10000"));
        paiementHorsPeriode.setDatePaiement(horsPeriode);

        when(paiementRepository.findPaiementByVillage(1L))
                .thenReturn(List.of(paiementDansLaPeriode, paiementHorsPeriode));

        Penalite penaliteDansLaPeriode = penalite(membre, StatutPenalite.PAYEE, "1000");
        penaliteDansLaPeriode.setDate(dansLaPeriode);
        Penalite penaliteHorsPeriode = penalite(membre, StatutPenalite.NON_PAYEE, "2000");
        penaliteHorsPeriode.setDate(horsPeriode);
        when(penaliteRepository.findByVillageId(1L))
                .thenReturn(List.of(penaliteDansLaPeriode, penaliteHorsPeriode));

        Reunion reunionDansLaPeriode = new Reunion();
        reunionDansLaPeriode.setVillage(village);
        reunionDansLaPeriode.setDateHeure(dansLaPeriode.atTime(10, 0));
        reunionDansLaPeriode.setLieu("Place du village");

        Reunion reunionHorsPeriode = new Reunion();
        reunionHorsPeriode.setVillage(village);
        reunionHorsPeriode.setDateHeure(horsPeriode.atTime(10, 0));
        reunionHorsPeriode.setLieu("Ecole");

        when(reunionRepository.findByVillageId(1L))
                .thenReturn(List.of(reunionDansLaPeriode, reunionHorsPeriode));

        when(presenceRepository.findByVillageId(1L)).thenReturn(List.of(
                presence(membre, true, reunionDansLaPeriode),
                presence(membre, false, reunionHorsPeriode)
        ));

        PresidentDashboardResponseDTO dto = dashboardService.getPresidentDashboard(
                1L, president, LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 31));

        assertThat(dto.montantTotalCollecte()).isEqualByComparingTo("10000"); // seul mars compte
        assertThat(dto.montantTotalPenalites()).isEqualByComparingTo("1000");
        assertThat(dto.montantTotalPenalitesNonPayees()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(dto.montantCaisse()).isEqualByComparingTo("11000"); // paiement + pénalité payée de mars
        assertThat(dto.nombreReunions()).isEqualTo(1);
        assertThat(dto.tauxPresence()).isEqualTo(100.0d); // seule la présence de mars (présent) compte
    }

    @Test
    void getPresidentDashboard_renvoieConfigNullEtProchaineReunionNullQuandAbsentes() {
        Utilisateur president = utilisateur(1L, "PRESIDENT");

        when(villageAccessService.verifierEtRecupererVillageGere(1L, president)).thenReturn(village);
        when(utilisateurRepository.findByVillageId(1L)).thenReturn(List.of());
        when(configurationVillageRepository.findByVillageId(1L)).thenReturn(java.util.Optional.empty());
        when(paiementRepository.findPaiementByVillage(1L)).thenReturn(List.of());
        when(penaliteRepository.findByVillageId(1L)).thenReturn(List.of());
        when(reunionRepository.findByVillageId(1L)).thenReturn(List.of());
        when(presenceRepository.findByVillageId(1L)).thenReturn(List.of());

        PresidentDashboardResponseDTO dto = dashboardService.getPresidentDashboard(1L, president, null, null);

        assertThat(dto.montantCotisationMensuelle()).isNull();
        assertThat(dto.prochaineReunion()).isNull();
        assertThat(dto.tauxPresence()).isZero();
        assertThat(dto.montantCaisse()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void getPresidentDashboard_delegueLeControleDAccesAVillageAccessService() {
        Utilisateur autrePresident = utilisateur(9L, "PRESIDENT");
        RuntimeException refus = new RuntimeException("acces refuse");
        when(villageAccessService.verifierEtRecupererVillageGere(1L, autrePresident)).thenThrow(refus);

        assertThatThrownBy(() -> dashboardService.getPresidentDashboard(1L, autrePresident, null, null))
                .isSameAs(refus);

        verify(utilisateurRepository, org.mockito.Mockito.never()).findByVillageId(any());
    }

    // ---------------------------------------------------------------------
    // getMembreDashboard
    // ---------------------------------------------------------------------

    @Test
    void getMembreDashboard_calculeLaVuePersonnelleDuMembreConnecte() {
        Utilisateur membre = utilisateur(1L, "MEMBRE");
        membre.setVillage(village);

        LocalDate aujourdHui = LocalDate.now();
        int moisCourant = aujourdHui.getMonthValue();
        int anneeCourante = aujourdHui.getYear();

        ConfigurationVillage config = new ConfigurationVillage();
        config.setVillage(village);
        config.setMontantCotisationMensuelle(new BigDecimal("10000"));
        config.setMontantPenaliteRetard(new BigDecimal("1000"));
        config.setMontantPenaliteAbsenceReunion(new BigDecimal("500"));
        when(configurationVillageRepository.findByVillageId(1L)).thenReturn(java.util.Optional.of(config));

        when(paiementRepository.findByUtilisateurId(1L)).thenReturn(List.of(
                paiement(membre, moisCourant, anneeCourante, "10000")
        ));
        when(penaliteRepository.findByUtilisateurId(1L)).thenReturn(List.of(
                penalite(membre, StatutPenalite.NON_PAYEE, "1000")
        ));

        Reunion reunionFuture = new Reunion();
        reunionFuture.setVillage(village);
        reunionFuture.setDateHeure(LocalDateTime.now().plusDays(2));
        reunionFuture.setLieu("Ecole du village");
        when(reunionRepository.findByVillageId(1L)).thenReturn(List.of(reunionFuture));

        when(presenceRepository.findByUtilisateurId(1L)).thenReturn(List.of(
                presence(membre, true),
                presence(membre, false),
                presence(membre, true)
        ));

        MembreDashboardResponseDTO dto = dashboardService.getMembreDashboard(membre);

        assertThat(dto.village().id()).isEqualTo(1L);
        assertThat(dto.moisCourant()).isEqualTo(moisCourant);
        assertThat(dto.anneeCourante()).isEqualTo(anneeCourante);
        assertThat(dto.montantCotisationMensuelle()).isEqualByComparingTo("10000");
        assertThat(dto.aPayeMoisCourant()).isTrue();
        assertThat(dto.montantTotalPaye()).isEqualByComparingTo("10000");
        assertThat(dto.nombrePaiements()).isEqualTo(1L);
        assertThat(dto.nombrePenalitesNonPayees()).isEqualTo(1L);
        assertThat(dto.montantTotalPenalitesNonPayees()).isEqualByComparingTo("1000");
        assertThat(dto.prochaineReunion()).isNotNull();
        assertThat(dto.nombrePresences()).isEqualTo(2L);
        assertThat(dto.nombreAbsences()).isEqualTo(1L);
        assertThat(dto.tauxPresence()).isCloseTo(66.66d, org.assertj.core.data.Offset.offset(0.01d));
    }

    @Test
    void getMembreDashboard_aPayeMoisCourantFauxQuandAucunPaiementCeMois() {
        Utilisateur membre = utilisateur(1L, "MEMBRE");
        membre.setVillage(village);

        when(configurationVillageRepository.findByVillageId(1L)).thenReturn(java.util.Optional.empty());
        when(paiementRepository.findByUtilisateurId(1L)).thenReturn(List.of(
                paiement(membre, 1, 2020, "10000") // mois/année passés, jamais le mois courant
        ));
        when(penaliteRepository.findByUtilisateurId(1L)).thenReturn(List.of());
        when(reunionRepository.findByVillageId(1L)).thenReturn(List.of());
        when(presenceRepository.findByUtilisateurId(1L)).thenReturn(List.of());

        MembreDashboardResponseDTO dto = dashboardService.getMembreDashboard(membre);

        assertThat(dto.aPayeMoisCourant()).isFalse();
        assertThat(dto.montantCotisationMensuelle()).isNull();
        assertThat(dto.tauxPresence()).isZero();
    }

    @Test
    void getMembreDashboard_leveUneExceptionQuandLeMembreNAAucunVillage() {
        Utilisateur membreSansVillage = utilisateur(1L, "MEMBRE");
        membreSansVillage.setVillage(null);

        assertThatThrownBy(() -> dashboardService.getMembreDashboard(membreSansVillage))
                .isInstanceOf(UtilisateurSansVillageException.class);
    }
}
