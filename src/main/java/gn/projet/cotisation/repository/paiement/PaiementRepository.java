package gn.projet.cotisation.repository.paiement;

import gn.projet.cotisation.entity.Paiement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaiementRepository extends JpaRepository<Paiement, Long> {

    @Query(value = "SELECT p.* FROM paiements p " +
            "JOIN utilisateurs u ON p.utilisateur_id = u.id " +
            "WHERE u.village_id = :villageId", nativeQuery = true
    )
    List<Paiement> findPaiementByVillage(@Param("villageId") Long villageId);

    // Tous les paiements d'UN membre précis, utilisé par le tableau de bord du membre connecté.
    @Query(value = "SELECT * FROM paiements WHERE utilisateur_id = :utilisateurId", nativeQuery = true)
    List<Paiement> findByUtilisateurId(@Param("utilisateurId") Long utilisateurId);

    // Reflète la contrainte unique (utilisateur_id, mois, annee) de l'entité Paiement : utilisé pour
    // vérifier AVANT l'insertion qu'un mois n'est pas déjà payé, plutôt que de laisser la base rejeter.
    boolean existsByUtilisateur_IdAndMoisAndAnnee(Long utilisateurId, Integer mois, Integer annee);
}
