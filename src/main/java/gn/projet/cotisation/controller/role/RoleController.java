package gn.projet.cotisation.controller.role;

import gn.projet.cotisation.dto.roles.RoleResponseDTO; // DTO renvoyé au client
import gn.projet.cotisation.dto.roles.UpdateRoleRequestDTO; // DTO reçu du client
import gn.projet.cotisation.service.role.RoleService; // logique métier, injectée dans ce contrôleur
import org.springframework.http.HttpStatus; // énumération des codes HTTP (200, 404, 204...)
import org.springframework.web.bind.annotation.DeleteMapping; // route HTTP DELETE
import org.springframework.web.bind.annotation.GetMapping; // route HTTP GET
import org.springframework.web.bind.annotation.PathVariable; // extrait une valeur depuis l'URL ({id})
import org.springframework.web.bind.annotation.PostMapping; // route HTTP POST
import org.springframework.web.bind.annotation.PutMapping; // route HTTP PUT
import org.springframework.web.bind.annotation.RequestBody; // convertit le JSON reçu en objet Java
import org.springframework.web.bind.annotation.RequestMapping; // préfixe d'URL commun à toute la classe
import org.springframework.web.bind.annotation.ResponseStatus; // impose un code HTTP précis à une méthode
import org.springframework.web.bind.annotation.RestController; // @Controller + @ResponseBody (renvoie du JSON)

import java.util.List;

// Contrôleur simplifié : plus de gestion manuelle du cas "absent" (ResponseEntity.notFound()...).
// Le service lève RoleNotFoundException quand nécessaire, GlobalExceptionHandler la transforme en 404.
@RestController // chaque méthode renvoie directement du JSON, pas une vue HTML
@RequestMapping("/api/roles") // toutes les routes ci-dessous commencent par /api/roles
public class RoleController {

    private final RoleService roleService; // seule dépendance : le contrôleur ne connaît QUE le service

    // Spring injecte automatiquement l'implémentation de RoleService (RoleServiceImpl) ici au démarrage
    public RoleController(RoleService roleService) {
        this.roleService = roleService; // on garde la référence dans le champ de la classe
    }

    // GET /api/roles -> liste tous les rôles
    @GetMapping
    public List<RoleResponseDTO> getAll() {
        return roleService.findAll(); // délègue tout le travail au service, renvoyé tel quel en JSON
    }

    // GET /api/roles/{id} -> 200 avec le rôle, ou 404 automatique si RoleNotFoundException est levée
    @GetMapping("/{id}")
    public RoleResponseDTO getById(@PathVariable Long id) { // {id} dans l'URL -> paramètre "id" converti en Long
        return roleService.findById(id); // si absent, l'exception interrompt ici et part vers le handler global
    }

    // POST /api/roles -> crée un rôle
    @PostMapping
    public RoleResponseDTO create(@RequestBody UpdateRoleRequestDTO dto) { // JSON du corps -> UpdateRoleRequestDTO
        return roleService.create(dto); // renvoie le rôle créé (avec son id généré) en JSON
    }

    // PUT /api/roles/{id} -> 200 avec le rôle mis à jour, ou 404 automatique si absent
    @PutMapping("/{id}")
    public RoleResponseDTO update(@PathVariable Long id, @RequestBody UpdateRoleRequestDTO dto) {
        return roleService.update(id, dto); // id vient de l'URL, nouvelles valeurs viennent du corps JSON
    }

    // DELETE /api/roles/{id}
    // @ResponseStatus(NO_CONTENT) : comme la méthode ne renvoie rien (void), on dit explicitement
    // à Spring quel code HTTP répondre en cas de succès (204). En cas d'échec (id absent), l'exception
    // levée par le service prend le dessus et GlobalExceptionHandler renvoie 404 à la place.
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // 204 = succès, rien à renvoyer dans le corps de la réponse
    public void delete(@PathVariable Long id) {
        roleService.delete(id); // aucune valeur de retour : soit ça réussit (204), soit ça lève une exception
    }
}
