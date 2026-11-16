package gn.projet.cotisation.repository.typemouvementcaisse;

import gn.projet.cotisation.entity.TypeMouvementCaisse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TypeMouvementCaisseRepository extends JpaRepository<TypeMouvementCaisse, Long> {

    @Query(value = "SELECT * FROM types_mouvement_caisse WHERE village_id = :villageId ORDER BY libelle",
            nativeQuery = true)
    List<TypeMouvementCaisse> findByVillageId(@Param("villageId") Long villageId);

    // Vérifié AVANT l'insertion/la modification (voir TypeMouvementCaisseServiceImpl) pour renvoyer
    // un message clair, plutôt que de laisser la contrainte unique (village_id, libelle) de la base
    // le faire. IgnoreCase : "mariage" et "Mariage" comptent comme le même libellé pour ce village.
    boolean existsByVillage_IdAndLibelleIgnoreCase(Long villageId, String libelle);
}
