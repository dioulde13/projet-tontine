package gn.projet.cotisation.entity;

// Annotations JPA (jakarta.persistence) : elles décrivent comment cette classe correspond à une table SQL
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// Annotations Lombok : génèrent automatiquement du code répétitif (getters, setters, constructeurs)
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Dit à Hibernate/JPA que cette classe doit être mappée sur une table de la base de données
@Entity
// Nom exact de la table en base
@Table(name = "villages")
// Lombok génère tous les getters (getId(), getNom()...)
@Getter
// Lombok génère tous les setters (setId(...), setNom(...)...)
@Setter
// Lombok génère un constructeur vide Village() — obligatoire pour que JPA puisse instancier l'entité
@NoArgsConstructor
// Lombok génère un constructeur avec tous les champs Village(id, nom)
@AllArgsConstructor
public class Village extends BaseEntity {

    // Clé primaire de la table
    @Id
    // La valeur est générée automatiquement par la base (auto-increment)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Colonne "nom" : obligatoire (nullable = false) et unique (deux villages ne peuvent pas porter le même nom)
    @Column(nullable = false, unique = true)
    private String nom; // ex: "Kindia", "Mamou"
}
