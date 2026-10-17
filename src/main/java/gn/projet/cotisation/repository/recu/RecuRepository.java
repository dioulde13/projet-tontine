package gn.projet.cotisation.repository.recu;

import gn.projet.cotisation.entity.Recu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecuRepository extends JpaRepository<Recu, Long> {

    // Un reçu peut désormais couvrir plusieurs paiements (table de jointure recu_paiements) :
    // on vérifie via une sous-requête JPQL qu'AUCUN reçu existant ne contient déjà ce paiement,
    // avant d'en créer un nouveau.
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Recu r JOIN r.paiements p WHERE p.id = :paiementId")
    boolean existsByPaiementId(@Param("paiementId") Long paiementId);

    // Même principe que PaiementRepository.findPaiementByVillage : on remonte de recus, via la
    // table de jointure recu_paiements, à paiements, puis à utilisateurs, pour filtrer sur le
    // village. DISTINCT car un reçu à plusieurs paiements produirait sinon une ligne par paiement.
    @Query(value = "SELECT DISTINCT r.* FROM recus r " +
            "JOIN recu_paiements rp ON rp.recu_id = r.id " +
            "JOIN paiements p ON rp.paiement_id = p.id " +
            "JOIN utilisateurs u ON p.utilisateur_id = u.id " +
            "WHERE u.village_id = :villageId", nativeQuery = true
    )
    List<Recu> findRecuByVillage(@Param("villageId") Long villageId);
}
