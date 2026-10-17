package gn.projet.cotisation.service.paiement;

import gn.projet.cotisation.dto.paiements.CreatePaiementRequestDTO;
import gn.projet.cotisation.dto.paiements.PaiementResponseDTO;
import gn.projet.cotisation.entity.ConfigurationVillage;
import gn.projet.cotisation.entity.Mois;
import gn.projet.cotisation.entity.Paiement;
import gn.projet.cotisation.entity.Role;
import gn.projet.cotisation.entity.Utilisateur;
import gn.projet.cotisation.entity.Village;
import gn.projet.cotisation.exception.paiement.PaiementDejaExistantException;
import gn.projet.cotisation.repository.configurationVillage.ConfigurationVillageRepository;
import gn.projet.cotisation.repository.paiement.PaiementRepository;
import gn.projet.cotisation.repository.penalite.PenaliteRepository;
import gn.projet.cotisation.repository.utilisateur.UtilisateurRepository;
import gn.projet.cotisation.service.village.VillageAccessService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaiementServiceImplTest {

    @Mock
    private PaiementRepository paiementRepository;
    @Mock
    private UtilisateurRepository utilisateurRepository;
    @Mock
    private VillageAccessService villageAccessService;
    @Mock
    private ConfigurationVillageRepository configurationVillageRepository;
    @Mock
    private PenaliteRepository penaliteRepository;

    @InjectMocks
    private PaiementServiceImpl paiementService;

    private Village village;
    private Utilisateur president;
    private Utilisateur membre;
    private ConfigurationVillage config;

    @BeforeEach
    void setUp() {
        village = new Village(1L, "Kindia");

        president = new Utilisateur();
        president.setId(1L);
        president.setNom("Pres");
        president.setPrenom("Ident");
        president.setTelephone("600000000");
        president.setMotDePasse("hash");
        president.getRoles().add(role("PRESIDENT"));

        membre = new Utilisateur();
        membre.setId(2L);
        membre.setNom("Membre");
        membre.setPrenom("Un");
        membre.setTelephone("600000001");
        membre.setMotDePasse("hash");
        membre.setVillage(village);

        config = new ConfigurationVillage();
        config.setVillage(village);
        config.setMontantCotisationMensuelle(new BigDecimal("10000"));
        config.setMontantPenaliteRetard(new BigDecimal("1000"));
        config.setMontantPenaliteAbsenceReunion(new BigDecimal("500"));

        when(villageAccessService.verifierEtRecupererVillageGere(1L, president)).thenReturn(village);
        when(configurationVillageRepository.findByVillageId(1L)).thenReturn(Optional.of(config));
        when(utilisateurRepository.findById(2L)).thenReturn(Optional.of(membre));
        // lenient() : certains tests (ex. refus d'un paiement en doublon) ne déclenchent jamais save(),
        // ce qui ferait échouer les autres à la vérification stricte "stubbing inutilisé" de Mockito.
        lenient().when(paiementRepository.save(any(Paiement.class))).thenAnswer(invocation -> {
            Paiement p = invocation.getArgument(0);
            p.setId(99L);
            return p;
        });
    }

    private Role role(String nom) {
        Role role = new Role();
        role.setNom(nom);
        return role;
    }

    private int moisPasse() {
        // Un mois garanti dans le passé (janvier de l'année dernière) pour forcer enRetard = true.
        return 1;
    }

    private int anneePassee() {
        return YearMonth.now().minusYears(1).getYear();
    }

    @Test
    void create_appliqueLaPenaliteDeRetardParDefautQuandLeFlagEstAbsent() {
        CreatePaiementRequestDTO dto = new CreatePaiementRequestDTO(2L, Set.of(Mois.JANVIER), anneePassee(), null);

        List<PaiementResponseDTO> resultats = paiementService.create(1L, dto, president);

        assertThat(resultats).hasSize(1);
        verify(penaliteRepository).save(any());
    }

    @Test
    void create_appliqueLaPenaliteDeRetardQuandLeFlagEstVrai() {
        CreatePaiementRequestDTO dto = new CreatePaiementRequestDTO(2L, Set.of(Mois.JANVIER), anneePassee(), true);

        paiementService.create(1L, dto, president);

        ArgumentCaptor<gn.projet.cotisation.entity.Penalite> captor =
                ArgumentCaptor.forClass(gn.projet.cotisation.entity.Penalite.class);
        verify(penaliteRepository).save(captor.capture());
        assertThat(captor.getValue().getMontant()).isEqualByComparingTo("1000");
        assertThat(captor.getValue().getType()).isEqualTo(gn.projet.cotisation.entity.TypePenalite.RETARD_PAIEMENT);
    }

    @Test
    void create_nAppliquePasLaPenaliteDeRetardQuandLeFlagEstFaux() {
        CreatePaiementRequestDTO dto = new CreatePaiementRequestDTO(2L, Set.of(Mois.JANVIER), anneePassee(), false);

        List<PaiementResponseDTO> resultats = paiementService.create(1L, dto, president);

        assertThat(resultats).hasSize(1);
        verify(penaliteRepository, never()).save(any());
    }

    @Test
    void create_nAppliqueJamaisDePenaliteQuandLeMoisNEstPasEnRetardMemeSiLeFlagEstVrai() {
        int moisCourant = LocalDate.now().getMonthValue();
        Mois moisChoisi = Mois.values()[moisCourant - 1];
        CreatePaiementRequestDTO dto = new CreatePaiementRequestDTO(
                2L, Set.of(moisChoisi), LocalDate.now().getYear(), true);

        List<PaiementResponseDTO> resultats = paiementService.create(1L, dto, president);

        assertThat(resultats).hasSize(1);
        verify(penaliteRepository, never()).save(any());
    }

    @Test
    void create_refuseUnPaiementDejaExistantPourCeMembreCeMoisEtCetteAnnee() {
        int annee = LocalDate.now().getYear();
        when(paiementRepository.existsByUtilisateur_IdAndMoisAndAnnee(2L, 3, annee)).thenReturn(true);

        CreatePaiementRequestDTO dto = new CreatePaiementRequestDTO(2L, Set.of(Mois.MARS), annee, true);

        assertThatThrownBy(() -> paiementService.create(1L, dto, president))
                .isInstanceOf(PaiementDejaExistantException.class)
                .hasMessageContaining("Un Membre")
                .hasMessageContaining("3/" + annee);

        verify(paiementRepository, never()).save(any());
        verify(penaliteRepository, never()).save(any());
    }
}
