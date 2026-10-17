package gn.projet.cotisation.repository.utilisateur; // sous-package "utilisateur" du package repository

import gn.projet.cotisation.entity.Utilisateur; // l'entité JPA que ce repository va gérer
import org.springframework.data.jpa.repository.JpaRepository; // fournit save/findById/findAll/deleteById/etc.
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional; // type conteneur pour une valeur potentiellement absente

// Repository pour l'entité Utilisateur, clé primaire de type Long.
// Spring Data JPA génère automatiquement une implémentation de cette interface au démarrage.
@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

    // Requête générée automatiquement : "SELECT * FROM utilisateurs WHERE telephone = ?"
    // Utile par exemple pour vérifier si un numéro est déjà pris, ou pour l'authentification
    Optional<Utilisateur> findByTelephone(String telephone); // "telephone" doit correspondre au champ de l'entité

   @Query(value = "SELECT * FROM utilisateurs WHERE village_id =:villageId", nativeQuery = true)
   List<Utilisateur> findByVillageId(@Param("villageId") Long villageId);

}
