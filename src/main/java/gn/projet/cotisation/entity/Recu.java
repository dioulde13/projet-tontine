package gn.projet.cotisation.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "recus")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Recu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Numéro affiché sur le reçu remis au membre (ex: "REC-2026-00042").
    // unique = true : deux reçus ne peuvent jamais porter le même numéro.
    @Column(nullable = false, unique = true)
    private String numero;

    // Date à laquelle le reçu a été émis (= généré par le président), pas forcément la même
    // que la date du paiement si on édite le reçu plus tard qu'au moment du paiement.
    @Column(nullable = false)
    private LocalDate dateEmission;

    // @OneToMany unidirectionnel via table de jointure (Paiement n'a pas besoin de connaître son
    // Recu) : un reçu peut désormais couvrir PLUSIEURS paiements (ex: plusieurs mois réglés d'un
    // coup pour le même membre). unique = true sur paiement_id dans la table de jointure garantit
    // qu'un paiement donné ne peut jamais appartenir qu'à un seul reçu à la fois.
    @OneToMany
    @JoinTable(
            name = "recu_paiements",
            joinColumns = @JoinColumn(name = "recu_id"),
            inverseJoinColumns = @JoinColumn(name = "paiement_id", unique = true)
    )
    private List<Paiement> paiements = new ArrayList<>();
}
