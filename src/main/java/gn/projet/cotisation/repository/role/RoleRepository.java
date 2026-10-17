package gn.projet.cotisation.repository.role; // ce fichier appartient au sous-package "role" du package repository

import gn.projet.cotisation.entity.Role; // l'entité JPA que ce repository va gérer
// JpaRepository fournit déjà toutes les méthodes CRUD de base (save, findById, findAll, deleteById...)
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional; // type conteneur qui représente une valeur potentiellement absente (au lieu de null)

// "extends JpaRepository<Role, Long>" = ce repository gère l'entité Role, dont la clé primaire est de type Long.
// Spring Data JPA génère automatiquement l'implémentation de cette interface au démarrage (pas besoin d'écrire de code).
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    // Méthode "dérivée" : Spring Data JPA lit le nom de la méthode ("findByNom") et génère tout seul
    // la requête SQL équivalente à "SELECT * FROM roles WHERE nom = ?"
    // Optional<Role> = peut être vide si aucun rôle ne porte ce nom (évite de retourner null)
    Optional<Role> findByNom(String nom); // le paramètre "nom" doit correspondre au champ "nom" de l'entité Role
}
