package gn.projet.cotisation.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "depenses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Depense extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Une sortie d'argent n'est pas liée à un membre précis (contrairement à Paiement/Penalite) :
    // rattachée directement au village, sur le même principe que Reunion.
    @ManyToOne
    @JoinColumn(name = "village_id", nullable = false)
    private Village village;

    // Catégorie configurée par le président pour ce village (ex: Mariage, Baptême, Décès)
    @ManyToOne
    @JoinColumn(name = "type_mouvement_caisse_id", nullable = false)
    private TypeMouvementCaisse type;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal montant;

    // Motif libre, optionnel : vient préciser le type choisi (ex: "Mariage de Fatoumata Camara").
    // Pas de nullable = false ici : contrairement au type, il peut rester vide.
    @Column
    private String motif;

    @Column(nullable = false)
    private LocalDate date;

    // Numéro et date d'émission du reçu, générés automatiquement à la création (voir
    // DepenseServiceImpl.create). Contrairement au reçu de paiement (entité Recu + table de
    // jointure), pas de table séparée ici : une dépense correspond toujours à exactement un reçu,
    // jamais à un regroupement de plusieurs lignes.
    @Column(nullable = false, unique = true)
    private String numeroRecu;

    @Column(nullable = false)
    private LocalDate dateEmissionRecu;

    // Personne ayant effectivement retiré l'argent de la caisse pour cette dépense (imprimé sur le
    // reçu) — pas forcément le président qui enregistre, ni un Utilisateur du système.
    @Column(nullable = false, length = 150)
    private String nomComptable;
}
