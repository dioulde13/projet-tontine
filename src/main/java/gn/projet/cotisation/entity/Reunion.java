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

import java.time.LocalDateTime; // date ET heure (contrairement à LocalDate utilisé pour Paiement)

@Entity
@Table(name = "reunions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Reunion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Le village auquel cette réunion appartient (obligatoire : pas de réunion "inter-villages" dans ce modèle)
    @ManyToOne
    @JoinColumn(name = "village_id", nullable = false)
    private Village village;

    // Date et heure prévues de la réunion
    @Column(nullable = false)
    private LocalDateTime dateHeure;

    // Lieu de la réunion (texte libre, ex: "Chez le président", "École du village")
    @Column(nullable = false)
    private String lieu;
}
