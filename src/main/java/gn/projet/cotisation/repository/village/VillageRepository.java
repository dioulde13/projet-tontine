package gn.projet.cotisation.repository.village;

import gn.projet.cotisation.entity.Village;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VillageRepository extends JpaRepository<Village, Long> {

    // Requête générée automatiquement : "SELECT * FROM villages WHERE nom = ?"
    Optional<Village> findByNom(String nom);
}
