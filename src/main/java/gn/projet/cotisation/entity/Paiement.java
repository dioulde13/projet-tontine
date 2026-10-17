package gn.projet.cotisation.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint; // pour poser une contrainte d'unicité sur PLUSIEURS colonnes à la fois

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate; // type dédié pour une date sans heure (jour/mois/année)

@Entity
// uniqueConstraints : impossible d'avoir deux lignes avec le même (utilisateur_id, mois, annee) —
// empêche de payer deux fois le même mois pour le même membre.
@Table(
        name = "paiements",
        uniqueConstraints = @UniqueConstraint(columnNames = {"utilisateur_id", "mois", "annee"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Paiement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Le membre qui a effectué ce paiement (obligatoire : un paiement appartient toujours à quelqu'un)
    @ManyToOne
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;

    // Mois couvert par ce paiement (1 à 12) — pas de contrainte de valeur ici, à valider plus tard côté service
    @Column(nullable = false)
    private Integer mois;

    // Année couverte par ce paiement (ex: 2026)
    @Column(nullable = false)
    private Integer annee;

    // Montant réellement payé pour ce mois — une "photo" de ConfigurationVillage.montantCotisationMensuelle
    // au moment du paiement, pour ne jamais changer l'historique si la config est modifiée plus tard
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal montant;

    // Date à laquelle le paiement a réellement été enregistré (sert à déterminer s'il est en retard)
    @Column(nullable = false)
    private LocalDate datePaiement;
}
