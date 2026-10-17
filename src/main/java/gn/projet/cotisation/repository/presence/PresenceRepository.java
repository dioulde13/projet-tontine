package gn.projet.cotisation.repository.presence;

import gn.projet.cotisation.entity.Presence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PresenceRepository extends JpaRepository<Presence, Long> {

@Query(value = "SELECT * FROM presences WHERE reunion_id = :reunionId AND present = true", nativeQuery = true)
List<Presence> findPresentsByReunionId(@Param("reunionId") Long reunionId);


@Query(value = "SELECT * FROM presences WHERE reunion_id = :reunionId AND present = false", nativeQuery = true)
List<Presence> findAbsencesByReunionId(@Param("reunionId") Long reunionId);

// Toutes les présences enregistrées pour les réunions d'UN village, utilisé pour calculer
// le taux de présence global dans le tableau de bord du président.
@Query(value = "SELECT pr.* FROM presences pr " +
        "JOIN reunions r ON pr.reunion_id = r.id " +
        "WHERE r.village_id = :villageId", nativeQuery = true)
List<Presence> findByVillageId(@Param("villageId") Long villageId);

// Toutes les présences d'UN membre précis, utilisé par le tableau de bord du membre connecté.
@Query(value = "SELECT * FROM presences WHERE utilisateur_id = :utilisateurId", nativeQuery = true)
List<Presence> findByUtilisateurId(@Param("utilisateurId") Long utilisateurId);
}
