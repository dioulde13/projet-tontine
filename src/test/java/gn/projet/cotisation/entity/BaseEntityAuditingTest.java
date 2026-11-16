package gn.projet.cotisation.entity;

import gn.projet.cotisation.repository.village.VillageRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

// Vérifie l'audit JPA (BaseEntity + JpaAuditingConfig + SpringSecurityAuditorAware) de bout en bout,
// avec la vraie base de données du projet (pas de H2 ici : le projet n'en a jamais eu besoin jusqu'ici,
// voir CotisationApplicationTests qui s'appuie déjà sur la même base). @Transactional fait automatiquement
// un ROLLBACK à la fin de chaque test : aucune donnée n'est réellement laissée en base.
@SpringBootTest
@Transactional
class BaseEntityAuditingTest {

    @Autowired
    private VillageRepository villageRepository;

    @AfterEach
    void clearSecurityContext() {
        // Évite qu'un Authentication posé dans un test ne "fuite" vers le test suivant (même thread).
        SecurityContextHolder.clearContext();
    }

    private void authentifier(String telephone) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        telephone, null, List.<GrantedAuthority>of(new SimpleGrantedAuthority("ROLE_PRESIDENT"))));
    }

    @Test
    void save_renseigneCreatedAtEtCreatedByQuandUnUtilisateurEstAuthentifie() {
        authentifier("620000001");

        Village village = new Village();
        village.setNom("Audit-Test-Creation-" + System.nanoTime());
        Village saved = villageRepository.saveAndFlush(village);

        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getCreatedBy()).isEqualTo("620000001");
        // À la création, updatedAt/updatedBy sont renseignés avec les mêmes valeurs que created*.
        assertThat(saved.getUpdatedAt()).isEqualTo(saved.getCreatedAt());
        assertThat(saved.getUpdatedBy()).isEqualTo("620000001");
    }

    @Test
    void save_neRenseigneRienQuandPersonneNEstAuthentifie() {
        // Simule une requête non authentifiée (ex: inscription publique) : Spring Security pose un
        // AnonymousAuthenticationToken, jamais un contexte vide — SpringSecurityAuditorAware doit
        // explicitement écarter ce cas plutôt que d'enregistrer le "anonymousUser" générique.
        SecurityContextHolder.getContext().setAuthentication(
                new AnonymousAuthenticationToken(
                        "key", "anonymousUser", List.<GrantedAuthority>of(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))));

        Village village = new Village();
        village.setNom("Audit-Test-Anonyme-" + System.nanoTime());
        Village saved = villageRepository.saveAndFlush(village);

        assertThat(saved.getCreatedAt()).isNotNull(); // la DATE est connue même sans utilisateur
        assertThat(saved.getCreatedBy()).isNull();    // mais pas l'auteur : jamais de valeur inventée
        assertThat(saved.getUpdatedBy()).isNull();
    }

    @Test
    void update_renseigneUpdatedAtEtUpdatedBySansToucherCreatedAtEtCreatedBy() throws InterruptedException {
        authentifier("620000002");
        Village village = new Village();
        village.setNom("Audit-Test-Modification-" + System.nanoTime());
        Village saved = villageRepository.saveAndFlush(village);

        var createdAtOrigine = saved.getCreatedAt();
        var createdByOrigine = saved.getCreatedBy();

        // Garantit un horodatage strictement différent (la précision DB n'est pas instantanée à l'œil nu).
        Thread.sleep(5);

        authentifier("620000003"); // un AUTRE utilisateur effectue la modification
        saved.setNom(saved.getNom() + "-modifie");
        Village updated = villageRepository.saveAndFlush(saved);

        assertThat(updated.getCreatedAt()).isEqualTo(createdAtOrigine); // jamais écrasé
        assertThat(updated.getCreatedBy()).isEqualTo(createdByOrigine); // jamais écrasé
        assertThat(updated.getUpdatedBy()).isEqualTo("620000003");      // le dernier auteur, pas le premier
        assertThat(updated.getUpdatedAt()).isAfter(createdAtOrigine);
    }
}
