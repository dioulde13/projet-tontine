package gn.projet.cotisation.controller.utilisateur;

import gn.projet.cotisation.security.JwtService; // génère le token une fois l'identité vérifiée
import gn.projet.cotisation.dto.utilisateurs.LoginRequestDTO;
import gn.projet.cotisation.dto.utilisateurs.LoginResponseDTO;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationManager; // orchestre la vérification d'identité
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken; // représente une tentative "email + mot de passe"
import org.springframework.security.core.Authentication; // résultat d'une authentification réussie
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Seul contrôleur dont les routes sont (en partie) publiques : voir SecurityConfig, POST /api/auth/login
// est exempté de l'obligation d'authentification.
@RestController
@RequestMapping("/api/auth") // toutes les routes ci-dessous commencent par /api/auth
public class AuthController {

    private final AuthenticationManager authenticationManager; // vérifie effectivement email + mot de passe
    private final JwtService jwtService; // génère le token à renvoyer si la vérification réussit

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    // POST /api/auth/login -> vérifie telephone/motDePasse, renvoie un JWT si valides
    @PostMapping("/login")
    public LoginResponseDTO login(@Valid @RequestBody LoginRequestDTO request) {
        // authenticate() déclenche toute la vérification : appelle CustomUserDetailsService pour charger
        // l'utilisateur par téléphone, puis compare le mot de passe fourni au hash stocké via le PasswordEncoder.
        // Si les identifiants sont incorrects, une BadCredentialsException est levée automatiquement ici
        // (interceptée par GlobalExceptionHandler -> réponse 401).
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.telephone(), request.motDePasse())
        );

        // Si on arrive ici, l'authentification a réussi : le "principal" contient l'utilisateur vérifié
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtService.generateToken(userDetails); // génère un token signé pour cet utilisateur
        return new LoginResponseDTO(token); // renvoyé au client, qui devra l'utiliser sur ses prochaines requêtes
    }
}
