package gn.projet.cotisation.security;

import gn.projet.cotisation.repository.utilisateur.UtilisateurRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService; // contrat standard de Spring Security
import org.springframework.security.core.userdetails.UsernameNotFoundException; // exception attendue si absent
import org.springframework.stereotype.Service;

// Spring Security appelle automatiquement cette classe à chaque tentative de connexion, pour charger
// l'utilisateur correspondant à l'identifiant fourni (ici, le téléphone), AVANT de vérifier le mot de passe.
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UtilisateurRepository utilisateurRepository; // accès à la table utilisateurs

    public CustomUserDetailsService(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository; // injecté automatiquement par Spring
    }

    @Override
    public UserDetails loadUserByUsername(String telephone) throws UsernameNotFoundException {
        // "username" est le terme générique du contrat Spring Security ; chez nous il correspond au téléphone
        return utilisateurRepository.findByTelephone(telephone) // cherche l'Utilisateur par téléphone
                .map(CustomUserDetails::new) // si trouvé, l'enveloppe en CustomUserDetails
                .orElseThrow(() -> new UsernameNotFoundException("Aucun utilisateur avec le téléphone " + telephone));
    }
}
