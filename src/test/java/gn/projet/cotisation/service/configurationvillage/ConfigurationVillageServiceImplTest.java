package gn.projet.cotisation.service.configurationvillage;

import gn.projet.cotisation.dto.configurationvillages.ConfigurationVillageResponseDTO;
import gn.projet.cotisation.dto.configurationvillages.UpdateConfigurationVillageRequestDTO;
import gn.projet.cotisation.entity.ConfigurationVillage;
import gn.projet.cotisation.entity.Utilisateur;
import gn.projet.cotisation.entity.Village;
import gn.projet.cotisation.exception.configurationvillage.ConfigurationVillageNotFoundException;
import gn.projet.cotisation.repository.configurationVillage.ConfigurationVillageRepository;
import gn.projet.cotisation.service.village.VillageAccessService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConfigurationVillageServiceImplTest {

    @Mock
    private ConfigurationVillageRepository configurationVillageRepository;
    @Mock
    private VillageAccessService villageAccessService;

    @InjectMocks
    private ConfigurationVillageServiceImpl configurationVillageService;

    @Test
    void update_modifieLesMontantsDeLaConfigurationExistante() {
        Village village = new Village(1L, "Kindia");
        Utilisateur president = new Utilisateur();
        president.setId(1L);

        ConfigurationVillage configExistante = new ConfigurationVillage();
        configExistante.setId(10L);
        configExistante.setVillage(village);
        configExistante.setMontantCotisationMensuelle(new BigDecimal("10000"));
        configExistante.setMontantPenaliteRetard(new BigDecimal("1000"));
        configExistante.setMontantPenaliteAbsenceReunion(new BigDecimal("500"));

        when(villageAccessService.verifierEtRecupererVillageGere(1L, president)).thenReturn(village);
        when(configurationVillageRepository.findByVillageId(1L)).thenReturn(Optional.of(configExistante));
        when(configurationVillageRepository.save(configExistante)).thenReturn(configExistante);

        UpdateConfigurationVillageRequestDTO dto = new UpdateConfigurationVillageRequestDTO(
                new BigDecimal("15000"),
                new BigDecimal("2000"),
                new BigDecimal("800")
        );

        ConfigurationVillageResponseDTO result = configurationVillageService.update(1L, dto, president);

        assertThat(result.id()).isEqualTo(10L);
        assertThat(result.villageId()).isEqualTo(1L);
        assertThat(result.montantCotisationMensuelle()).isEqualByComparingTo("15000");
        assertThat(result.montantPenaliteRetard()).isEqualByComparingTo("2000");
        assertThat(result.montantPenaliteAbsenceReunion()).isEqualByComparingTo("800");

        ArgumentCaptor<ConfigurationVillage> captor = ArgumentCaptor.forClass(ConfigurationVillage.class);
        verify(configurationVillageRepository).save(captor.capture());
        assertThat(captor.getValue().getMontantCotisationMensuelle()).isEqualByComparingTo("15000");
    }

    @Test
    void update_leveUneExceptionQuandAucuneConfigurationNExistePourCeVillage() {
        Village village = new Village(1L, "Kindia");
        Utilisateur president = new Utilisateur();
        president.setId(1L);

        when(villageAccessService.verifierEtRecupererVillageGere(1L, president)).thenReturn(village);
        when(configurationVillageRepository.findByVillageId(1L)).thenReturn(Optional.empty());

        UpdateConfigurationVillageRequestDTO dto = new UpdateConfigurationVillageRequestDTO(
                new BigDecimal("15000"), new BigDecimal("2000"), new BigDecimal("800")
        );

        assertThatThrownBy(() -> configurationVillageService.update(1L, dto, president))
                .isInstanceOf(ConfigurationVillageNotFoundException.class);

        verify(configurationVillageRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void update_delegueLeControleDAccesAVillageAccessServiceAvantToutAutreTravail() {
        Utilisateur autrePresident = new Utilisateur();
        autrePresident.setId(2L);
        RuntimeException refus = new RuntimeException("acces refuse");

        when(villageAccessService.verifierEtRecupererVillageGere(1L, autrePresident)).thenThrow(refus);

        UpdateConfigurationVillageRequestDTO dto = new UpdateConfigurationVillageRequestDTO(
                new BigDecimal("15000"), new BigDecimal("2000"), new BigDecimal("800")
        );

        assertThatThrownBy(() -> configurationVillageService.update(1L, dto, autrePresident))
                .isSameAs(refus);

        verify(configurationVillageRepository, never()).findByVillageId(org.mockito.ArgumentMatchers.any());
    }
}
