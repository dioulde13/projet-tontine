package gn.projet.cotisation.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne; // relation un-à-un (une config appartient à exactement un village)
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal; // type précis pour manipuler des montants d'argent (jamais double/float, qui arrondit mal)

@Entity
@Table(name = "configuration_villages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConfigurationVillage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relation un-à-un : chaque ConfigurationVillage appartient à exactement UN Village,
    // et un Village a au plus UNE ConfigurationVillage (peut ne pas encore être configuré).
    // unique = true sur la colonne de clé étrangère : garantit qu'aucun village n'a deux configs.
    @OneToOne
    @JoinColumn(name = "village_id", nullable = false, unique = true)
    private Village village;

    // Montant que chaque membre du village doit payer par mois
    @Column(nullable = false, precision = 10, scale = 2) // jusqu'à 10 chiffres dont 2 après la virgule
    private BigDecimal montantCotisationMensuelle;

    // Montant de la pénalité appliquée automatiquement pour chaque mois de cotisation payé en retard
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal montantPenaliteRetard;

    // Montant de la pénalité appliquée automatiquement en cas d'absence à une réunion
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal montantPenaliteAbsenceReunion;
}
