package gn.projet.cotisation.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// "Association porteuse" au sens Merise : cette entité relie Reunion et Utilisateur, et porte
// elle-même une information propre à cette association précise : "present".
@Entity
// uniqueConstraints : un même membre ne peut avoir qu'UNE SEULE présence enregistrée par réunion
@Table(
        name = "presences",
        uniqueConstraints = @UniqueConstraint(columnNames = {"reunion_id", "utilisateur_id"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Presence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // La réunion concernée par cette présence
    @ManyToOne
    @JoinColumn(name = "reunion_id", nullable = false)
    private Reunion reunion;

    // Le membre concerné par cette présence
    @ManyToOne
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;

    // true = présent à la réunion, false = absent (déclenche automatiquement une Penalite ABSENCE_REUNION)
    @Column(nullable = false)
    private boolean present;
}
