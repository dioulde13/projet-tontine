package gn.projet.cotisation.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType; // précise COMMENT un enum est stocké en base (texte ou nombre)
import jakarta.persistence.Enumerated;
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
@Table(name = "penalites")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Penalite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Le membre concerné par cette pénalité (obligatoire)
    @ManyToOne
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;

    // EnumType.STRING : stocke la valeur en base comme le texte "RETARD_PAIEMENT" ou "ABSENCE_REUNION",
    // pas comme un simple numéro (0, 1...) — plus lisible en base et plus sûr si l'ordre des valeurs
    // de l'enum change un jour dans le code.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypePenalite type;

    // Montant de la pénalité — une "photo" de la config du village au moment où elle a été déclenchée
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal montant;

    // Date à laquelle la pénalité a été générée
    @Column(nullable = false)
    private LocalDate date;

    // Statut de paiement de la pénalité elle-même, distinct du Paiement de cotisation
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutPenalite statut;

    // Rempli UNIQUEMENT si type = RETARD_PAIEMENT : le Paiement en retard qui a déclenché cette pénalité.
    // nullable (par défaut) : reste vide pour une pénalité de type ABSENCE_REUNION.
    @ManyToOne
    @JoinColumn(name = "paiement_origine_id")
    private Paiement paiementOrigine;

    // Rempli UNIQUEMENT si type = ABSENCE_REUNION : la Presence (absence) qui a déclenché cette pénalité.
    // nullable (par défaut) : reste vide pour une pénalité de type RETARD_PAIEMENT.
    @ManyToOne
    @JoinColumn(name = "presence_origine_id")
    private Presence presenceOrigine;
}
