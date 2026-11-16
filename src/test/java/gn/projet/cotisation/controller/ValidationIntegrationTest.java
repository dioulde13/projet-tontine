package gn.projet.cotisation.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Preuve de bout en bout (vraie couche web, pas juste la logique du handler en isolation — voir
// GlobalExceptionHandlerTest pour ça) que @Valid sur un @RequestBody déclenche bien
// MethodArgumentNotValidException, et que GlobalExceptionHandler la transforme en 400 avec un
// message exploitable. Utilise deux endpoints PUBLICS (POST /auth/login, POST /utilisateurs — voir
// SecurityConfig) : aucun token JWT à fabriquer pour ce test.
// @Transactional (comme BaseEntityAuditingTest) : le test "requête valide" crée réellement un
// utilisateur via MockMvc dans la vraie base de dev — le ROLLBACK automatique en fin de test
// garantit qu'aucune ligne n'y reste.
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Transactional
class ValidationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void login_renvoie400EtListeLesChampsEnFauteQuandLeCorpsEstVide() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"telephone\":\"\",\"motDePasse\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", containsString("telephone")))
                .andExpect(jsonPath("$.message", containsString("motDePasse")));
    }

    @Test
    void creerUtilisateur_renvoie400QuandLeNomEstVideEtLeTelephoneMalFormee() throws Exception {
        mockMvc.perform(post("/api/utilisateurs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nom":"","prenom":"Test","telephone":"abc","motDePasse":null,"roleIds":[],"villageId":null,"villagesGeresIds":[]}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("nom")))
                .andExpect(jsonPath("$.message", containsString("telephone")));
    }

    @Test
    void creerUtilisateur_accepteUneRequeteValide() throws Exception {
        mockMvc.perform(post("/api/utilisateurs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nom":"Diallo","prenom":"Test-Validation","telephone":"620099%s","motDePasse":null,"roleIds":[],"villageId":null,"villagesGeresIds":[]}
                                """.formatted(System.nanoTime() % 1000)))
                .andExpect(status().isOk());
    }
}
