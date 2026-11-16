package gn.projet.cotisation.repository.depense;

import gn.projet.cotisation.entity.Depense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DepenseRepository extends JpaRepository<Depense, Long> {

    // Les plus récentes en premier : plus utile qu'un ordre d'insertion pour un registre de dépenses.
    @Query(value = "SELECT * FROM depenses WHERE village_id = :villageId ORDER BY date DESC, id DESC",
            nativeQuery = true)
    List<Depense> findByVillageId(@Param("villageId") Long villageId);
}
