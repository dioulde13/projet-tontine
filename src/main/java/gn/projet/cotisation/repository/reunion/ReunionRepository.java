package gn.projet.cotisation.repository.reunion;

import gn.projet.cotisation.entity.Reunion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReunionRepository extends JpaRepository<Reunion, Long> {

    @Query(value = "SELECT * FROM reunions WHERE village_id = :villageId", nativeQuery = true)
    List<Reunion> findByVillageId(@Param("villageId") Long villageId);
}
