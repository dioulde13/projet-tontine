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

@Entity
// uniqueConstraints : deux types ne peuvent pas porter le même libellé DANS LE MÊME village, mais
// deux villages différents peuvent chacun avoir un type "Mariage" (pas d'unicité globale, contrairement à Role).
@Table(
        name = "types_mouvement_caisse",
        uniqueConstraints = @UniqueConstraint(columnNames = {"village_id", "libelle"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TypeMouvementCaisse extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Chaque type appartient à exactement un village : configuré librement par le président de
    // CE village, jamais partagé entre villages (contrairement à Role, qui est global).
    @ManyToOne
    @JoinColumn(name = "village_id", nullable = false)
    private Village village;

    // Libellé proposé au président lors de la saisie d'une dépense (ex: "Mariage", "Baptême", "Décès")
    @Column(nullable = false)
    private String libelle;
}
