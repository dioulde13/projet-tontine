package gn.projet.cotisation.repository.penalite;

import gn.projet.cotisation.entity.Penalite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PenaliteRepository  extends JpaRepository<Penalite, Long> {
    @Query(value = "SELECT p.* FROM penalites p " +
            "JOIN utilisateurs u ON p.utilisateur_id = u.id " +
            "WHERE u.village_id = :villageId", nativeQuery = true)
    List<Penalite> findByVillageId(@Param("villageId") Long villageId);

    // Toutes les pénalités d'UN membre précis, utilisé par le tableau de bord du membre connecté.
    @Query(value = "SELECT * FROM penalites WHERE utilisateur_id = :utilisateurId", nativeQuery = true)
    List<Penalite> findByUtilisateurId(@Param("utilisateurId") Long utilisateurId);
}
