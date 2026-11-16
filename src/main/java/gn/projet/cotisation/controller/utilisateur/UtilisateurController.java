package gn.projet.cotisation.controller.utilisateur;

import gn.projet.cotisation.dto.mapper.VillageMapper; // conversion Village -> VillageResponseDTO
import gn.projet.cotisation.dto.utilisateurs.UpdateUtilisateurRequestDTO; // DTO reçu du client
import gn.projet.cotisation.dto.utilisateurs.UtilisateurResponseDTO; // DTO renvoyé au client
import gn.projet.cotisation.dto.villages.VillageResponseDTO;
import gn.projet.cotisation.security.CustomUserDetails; // représente l'utilisateur authentifié pour la requête en cours
import gn.projet.cotisation.service.utilisateur.UtilisateurService; // logique métier injectée
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus; // codes HTTP
import org.springframework.security.core.annotation.AuthenticationPrincipal; // injecte l'utilisateur connecté
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable; // extrait {id} depuis l'URL
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody; // JSON du corps -> objet Java
import org.springframework.web.bind.annotation.RequestMapping; // préfixe d'URL commun
import org.springframework.web.bind.annotation.ResponseStatus; // fixe le code HTTP d'une méthode
import org.springframework.web.bind.annotation.RestController; // renvoie du JSON, pas une vue HTML

import java.util.List;

// Contrôleur simplifié : plus de gestion manuelle des cas d'erreur. Le service lève
// UtilisateurNotFoundException (-> 404) ou TelephoneDejaUtiliseException (-> 409),
// GlobalExceptionHandler s'occupe de les transformer en réponse HTTP appropriée.
@RestController
@RequestMapping("/api/utilisateurs") // toutes les routes ci-dessous commencent par /api/utilisateurs
public class UtilisateurController {

    private final UtilisateurService utilisateurService; // seule dépendance du contrôleur

    // Spring injecte automatiquement UtilisateurServiceImpl ici au démarrage
    public UtilisateurController(UtilisateurService utilisateurService) {
        this.utilisateurService = utilisateurService; // référence gardée dans le champ
    }

    // GET /api/utilisateurs -> liste tous les utilisateurs
    @GetMapping
    public List<UtilisateurResponseDTO> getAll() {
        return utilisateurService.findAll(); // renvoyé tel quel en JSON (jamais le mot de passe, cf. le DTO)
    }

    // GET /api/utilisateurs/{id}
    @GetMapping("/{id}")
    public UtilisateurResponseDTO getById(@PathVariable Long id) { // {id} de l'URL converti en Long
        return utilisateurService.findById(id); // 404 auto si UtilisateurNotFoundException est levée
    }

    // GET /api/utilisateurs/moi/villages -> "la page intermédiaire" : les villages que gère
    // le PRESIDENT actuellement connecté (déduits du token JWT, pas d'un id dans l'URL).
    @GetMapping("/moi/villages")
    public List<VillageResponseDTO> mesVillagesGeres(@AuthenticationPrincipal CustomUserDetails userDetails) {
        // @AuthenticationPrincipal : Spring Security remplit ce paramètre avec l'objet posé dans
        // SecurityContextHolder par JwtAuthenticationFilter pour CETTE requête — donc déjà l'utilisateur
        // authentifié, rechargé frais depuis la base (villagesGeres à jour, pas besoin de re-requêter ici).
        return userDetails.getUtilisateur().getVillagesGeres().stream()
                .map(VillageMapper::toResponseDto)
                .toList();
    }

    // POST /api/utilisateurs -> 409 automatique si le téléphone est déjà pris
    @PostMapping
    public UtilisateurResponseDTO create(@Valid @RequestBody UpdateUtilisateurRequestDTO dto) { // corps JSON -> DTO
        return utilisateurService.create(dto); // renvoie l'utilisateur créé (sans motDePasse)
    }

    // PUT /api/utilisateurs/{id}
    @PutMapping("/{id}")
    public UtilisateurResponseDTO update(@PathVariable Long id, @Valid @RequestBody UpdateUtilisateurRequestDTO dto) {
        return utilisateurService.update(id, dto); // id depuis l'URL, nouvelles valeurs depuis le corps JSON
    }

    // DELETE /api/utilisateurs/{id}
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // 204 : succès, rien à renvoyer
    public void delete(@PathVariable Long id) {
        utilisateurService.delete(id); // succès -> 204 ; id absent -> exception -> 404 via le handler global
    }
}
