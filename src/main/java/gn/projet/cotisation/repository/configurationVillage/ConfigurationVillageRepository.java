package gn.projet.cotisation.repository.configurationVillage;


import gn.projet.cotisation.entity.ConfigurationVillage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfigurationVillageRepository extends JpaRepository<ConfigurationVillage, Long> {

    // Optional, pas List : village_id est "unique = true" sur l'entité -> au plus UNE ligne possible.
    @Query(value = "SELECT * FROM configuration_villages " +
            "WHERE village_id = :villageId", nativeQuery = true)
    Optional<ConfigurationVillage> findByVillageId(@Param("villageId") Long villageId);
}
