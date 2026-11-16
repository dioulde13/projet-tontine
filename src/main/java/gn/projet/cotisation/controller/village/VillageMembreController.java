package gn.projet.cotisation.controller.village;

import gn.projet.cotisation.dto.utilisateurs.CreateMembreRequestDTO; // DTO reçu du client
import gn.projet.cotisation.dto.utilisateurs.UtilisateurResponseDTO; // DTO renvoyé au client
import gn.projet.cotisation.security.CustomUserDetails; // représente le président authentifié pour la requête
import gn.projet.cotisation.service.utilisateur.UtilisateurService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j; // génère automatiquement le champ "log" utilisé ci-dessous
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Contrôleur dédié aux ressources imbriquées sous un village précis (ici : ses membres),
// séparé de VillageController (qui gère le CRUD sur Village lui-même) pour éviter un fichier
// fourre-tout — d'autres routes imbriquées viendront (paiements, réunions...).
// @Slf4j (Lombok) génère au moment de la compilation :
//     private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(VillageMembreController.class);
// "log" n'existe nulle part dans ce fichier tel qu'on l'écrit — c'est Lombok qui l'ajoute au bytecode généré.
@Slf4j
@RestController
@RequestMapping("/api/villages/{villageId}/membres")
public class VillageMembreController {

    private final UtilisateurService utilisateurService;

    public VillageMembreController(UtilisateurService utilisateurService) {
        this.utilisateurService = utilisateurService;
    }

    // POST /api/villages/{villageId}/membres -> crée un membre DANS ce village, pour le président connecté.
    // Protégé par défaut (anyRequest().authenticated() dans SecurityConfig) : il faut un token JWT valide.
    // Le service vérifie en plus que ce président gère bien CE village (403 sinon) — pas juste qu'il est connecté.
    @PostMapping
    public UtilisateurResponseDTO create(
            @PathVariable Long villageId, // {villageId} de l'URL
            @Valid @RequestBody CreateMembreRequestDTO dto, // corps JSON -> DTO
            @AuthenticationPrincipal CustomUserDetails userDetails // l'utilisateur authentifié pour cette requête
    ) {
        // {} = un "placeholder" (espace réservé) : SLF4J remplace chaque {} par l'argument correspondant,
        // dans l'ordre. Ne JAMAIS faire "villageId + dto" en concaténation : ça construirait la chaîne
        // même quand ce niveau de log est désactivé, ce qui coûte du temps pour rien.
        log.info("Requête reçue : POST /api/villages/{}/membres, telephone du nouveau membre = {}, appelé par = {} (id={})",
                villageId,               // vient de @PathVariable : extrait directement de l'URL
                dto.telephone(),         // vient de @RequestBody : extrait du corps JSON envoyé par le client
                userDetails.getUsername(), // vient de @AuthenticationPrincipal : déduit du token JWT envoyé dans le header
                // .getId() : un champ précis (le plus utile pour identifier la ligne en base), jamais l'entité
                // entière — Utilisateur n'a pas de toString() lisible, et contient motDePasse + des relations.
                userDetails.getUtilisateur().getId()
        );
        return utilisateurService.createMembre(villageId, dto, userDetails.getUtilisateur());
    }

    @GetMapping
    public List<UtilisateurResponseDTO> list(
            @PathVariable Long villageId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
      return utilisateurService.findMembresDuVIllage(villageId, userDetails.getUtilisateur());
    }
}
