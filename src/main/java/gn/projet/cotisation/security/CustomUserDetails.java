package gn.projet.cotisation.security;

import gn.projet.cotisation.entity.Utilisateur; // notre entité métier
import org.springframework.security.core.GrantedAuthority; // représente un "droit" (ici, un rôle)
import org.springframework.security.core.authority.SimpleGrantedAuthority; // implémentation simple de GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails; // le contrat attendu par Spring Security

import java.util.Collection;
import java.util.stream.Collectors;

// Spring Security ne connaît pas notre entité Utilisateur : il travaille avec son propre contrat
// UserDetails (email, mot de passe haché, rôles, état du compte). Cette classe "adapte" un Utilisateur
// en UserDetails — c'est le design pattern Adapter, sans dupliquer les données.
public class CustomUserDetails implements UserDetails {

    private final Utilisateur utilisateur; // l'entité métier qu'on enveloppe, jamais copiée

    public CustomUserDetails(Utilisateur utilisateur) {
        this.utilisateur = utilisateur; // on garde juste la référence
    }

    // Donne accès à l'entité métier complète (roles, village, villagesGeres...) — utile dans les
    // contrôleurs qui reçoivent ce CustomUserDetails via @AuthenticationPrincipal et ont besoin
    // de plus que ce que l'interface UserDetails expose (juste username/password/authorities).
    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    @Override
    public String getUsername() {
        return utilisateur.getTelephone(); // dans notre appli, le téléphone sert d'identifiant de connexion
    }

    @Override
    public String getPassword() {
        return utilisateur.getMotDePasse(); // le mot de passe HACHÉ stocké en base (jamais en clair)
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Spring Security attend des autorités préfixées "ROLE_" par convention (ex: ROLE_ADMIN)
        // pour que des vérifications comme hasRole("ADMIN") fonctionnent correctement.
        // Nos rôles en base restent "propres" (ex: "ADMIN"), le préfixe est ajouté seulement ici.
        return utilisateur.getRoles().stream() // parcourt les entités Role attribuées à l'utilisateur
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getNom())) // "ADMIN" -> "ROLE_ADMIN"
                .collect(Collectors.toSet()); // regroupe le tout dans un Set (pas de doublon)
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // pas de gestion d'expiration de compte dans ce projet : toujours valide
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // pas de gestion de verrouillage de compte : toujours déverrouillé
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // pas d'expiration de mot de passe forcée
    }

    @Override
    public boolean isEnabled() {
        return true; // pas de désactivation de compte : toujours actif
    }
}
