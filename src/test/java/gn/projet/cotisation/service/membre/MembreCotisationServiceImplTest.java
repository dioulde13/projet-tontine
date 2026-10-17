package gn.projet.cotisation.service.membre;

import gn.projet.cotisation.dto.membre.MembreCotisationsResponseDTO;
import gn.projet.cotisation.entity.ConfigurationVillage;
import gn.projet.cotisation.entity.Paiement;
import gn.projet.cotisation.entity.Penalite;
import gn.projet.cotisation.entity.StatutPenalite;
import gn.projet.cotisation.entity.TypePenalite;
import gn.projet.cotisation.entity.Utilisateur;
import gn.projet.cotisation.entity.Village;
import gn.projet.cotisation.exception.utilisateur.UtilisateurSansVillageException;
import gn.projet.cotisation.repository.configurationVillage.ConfigurationVillageRepository;
import gn.projet.cotisation.repository.paiement.PaiementRepository;
import gn.projet.cotisation.repository.penalite.PenaliteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MembreCotisationServiceImplTest {

    @Mock
    private PaiementRepository paiementRepository;
    @Mock
    private PenaliteRepository penaliteRepository;
    @Mock
    private ConfigurationVillageRepository configurationVillageRepository;

    @InjectMocks
    private MembreCotisationServiceImpl membreCotisationService;

    private Village village;
    private Utilisateur membre;

    @BeforeEach
    void setUp() {
        village = new Village(1L, "Kindia");
        membre = new Utilisateur();
        membre.setId(1L);
        membre.setNom("Diallo");
        membre.setPrenom("Aissatou");
        membre.setTelephone("600000001");
        membre.setMotDePasse("hash");
        membre.setVillage(village);
    }

    private Paiement paiement(int mois, int annee, String montant) {
        Paiement paiement = new Paiement();
        paiement.setUtilisateur(membre);
        paiement.setMois(mois);
        paiement.setAnnee(annee);
        paiement.setMontant(new BigDecimal(montant));
        paiement.setDatePaiement(LocalDate.of(annee, mois, 1));
        return paiement;
    }

    private Penalite penalite(TypePenalite type, StatutPenalite statut, String montant, LocalDate date) {
        Penalite penalite = new Penalite();
        penalite.setUtilisateur(membre);
        penalite.setType(type);
        penalite.setMontant(new BigDecimal(montant));
        penalite.setDate(date);
        penalite.setStatut(statut);
        return penalite;
    }

    @Test
    void getMesCotisations_calculeLesMoisImpayesDeLAnneeCouranteEtTrieLesListes() {
        LocalDate aujourdHui = LocalDate.now();
        int anneeCourante = aujourdHui.getYear();
        int moisCourant = aujourdHui.getMonthValue();

        ConfigurationVillage config = new ConfigurationVillage();
        config.setVillage(village);
        config.setMontantCotisationMensuelle(new BigDecimal("10000"));
        when(configurationVillageRepository.findByVillageId(1L)).thenReturn(Optional.of(config));

        // Un seul mois payé cette année : janvier. Tous les autres mois jusqu'au mois courant sont dus.
        when(paiementRepository.findByUtilisateurId(1L)).thenReturn(List.of(
                paiement(1, anneeCourante, "10000"),
                paiement(12, anneeCourante - 1, "10000") // année passée : ne compte pas comme "dû" cette année
        ));

        when(penaliteRepository.findByUtilisateurId(1L)).thenReturn(List.of(
                penalite(TypePenalite.RETARD_PAIEMENT, StatutPenalite.NON_PAYEE, "1000", LocalDate.of(anneeCourante, 2, 1)),
                penalite(TypePenalite.ABSENCE_REUNION, StatutPenalite.PAYEE, "500", LocalDate.of(anneeCourante, 3, 1))
        ));

        MembreCotisationsResponseDTO dto = membreCotisationService.getMesCotisations(membre, null);

        assertThat(dto.montantCotisationMensuelle()).isEqualByComparingTo("10000");
        assertThat(dto.paiements()).hasSize(2);
        assertThat(dto.paiements().get(0).mois()).isEqualTo(1); // l'année courante trie avant l'année passée
        assertThat(dto.montantTotalPaye()).isEqualByComparingTo("20000");

        int nombreMoisDus = moisCourant - 1; // tous sauf janvier
        assertThat(dto.moisImpayes()).hasSize(nombreMoisDus);
        assertThat(dto.moisImpayes()).noneMatch(m -> m.mois() == 1);
        assertThat(dto.montantTotalDu()).isEqualByComparingTo(new BigDecimal("10000").multiply(BigDecimal.valueOf(nombreMoisDus)));

        assertThat(dto.penalites()).hasSize(2);
        assertThat(dto.penalites().get(0).type()).isEqualTo(TypePenalite.ABSENCE_REUNION); // la plus récente d'abord
        assertThat(dto.montantTotalPenalitesNonPayees()).isEqualByComparingTo("1000");
    }

    @Test
    void getMesCotisations_renvoieMoisImpayesVideQuandConfigurationAbsente() {
        when(configurationVillageRepository.findByVillageId(1L)).thenReturn(Optional.empty());
        when(paiementRepository.findByUtilisateurId(1L)).thenReturn(List.of());
        when(penaliteRepository.findByUtilisateurId(1L)).thenReturn(List.of());

        MembreCotisationsResponseDTO dto = membreCotisationService.getMesCotisations(membre, null);

        assertThat(dto.montantCotisationMensuelle()).isNull();
        assertThat(dto.moisImpayes()).isEmpty();
        assertThat(dto.montantTotalDu()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void getMesCotisations_leveUneExceptionQuandLeMembreNAAucunVillage() {
        Utilisateur membreSansVillage = new Utilisateur();
        membreSansVillage.setId(2L);
        membreSansVillage.setVillage(null);

        assertThatThrownBy(() -> membreCotisationService.getMesCotisations(membreSansVillage, null))
                .isInstanceOf(UtilisateurSansVillageException.class);
    }

    @Test
    void getMesCotisations_calculeLes12MoisDUneAnneePasseeCommeDus() {
        int anneePassee = LocalDate.now().getYear() - 1;

        ConfigurationVillage config = new ConfigurationVillage();
        config.setVillage(village);
        config.setMontantCotisationMensuelle(new BigDecimal("10000"));
        when(configurationVillageRepository.findByVillageId(1L)).thenReturn(Optional.of(config));

        // Seul juin de l'année passée est payé : les 11 autres mois de cette année sont dus.
        when(paiementRepository.findByUtilisateurId(1L)).thenReturn(List.of(paiement(6, anneePassee, "10000")));
        when(penaliteRepository.findByUtilisateurId(1L)).thenReturn(List.of());

        MembreCotisationsResponseDTO dto = membreCotisationService.getMesCotisations(membre, anneePassee);

        assertThat(dto.moisImpayes()).hasSize(11);
        assertThat(dto.moisImpayes()).allMatch(m -> m.annee() == anneePassee);
        assertThat(dto.moisImpayes()).noneMatch(m -> m.mois() == 6);
    }

    @Test
    void getMesCotisations_neRenvoieAucunMoisDuPourUneAnneeFuture() {
        int anneeFuture = LocalDate.now().getYear() + 1;

        ConfigurationVillage config = new ConfigurationVillage();
        config.setVillage(village);
        config.setMontantCotisationMensuelle(new BigDecimal("10000"));
        when(configurationVillageRepository.findByVillageId(1L)).thenReturn(Optional.of(config));
        when(paiementRepository.findByUtilisateurId(1L)).thenReturn(List.of());
        when(penaliteRepository.findByUtilisateurId(1L)).thenReturn(List.of());

        MembreCotisationsResponseDTO dto = membreCotisationService.getMesCotisations(membre, anneeFuture);

        assertThat(dto.moisImpayes()).isEmpty();
        assertThat(dto.montantTotalDu()).isEqualByComparingTo(BigDecimal.ZERO);
    }
}
