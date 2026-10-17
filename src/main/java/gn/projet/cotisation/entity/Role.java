package gn.projet.cotisation.entity;

// Annotations JPA (jakarta.persistence) : elles décrivent comment cette classe correspond à une table SQL
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// Annotations Lombok : génèrent automatiquement du code répétitif (getters, setters, constructeur vide)
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Dit à Hibernate/JPA que cette classe doit être mappée sur une table de la base de données
@Entity
// Nom exact de la table en base ("roles" plutôt que "role", pour éviter un conflit avec le mot-clé SQL ROLE)
@Table(name = "roles")
// Lombok génère tous les getters (getId(), getNom()...)
@Getter
// Lombok génère tous les setters (setId(...), setNom(...)...)
@Setter
// Lombok génère un constructeur vide Role() — obligatoire pour qu'Hibernate puisse instancier l'entité
// avant de remplir ses champs lui-même. Utilisé aussi explicitement dans RoleMapper.toEntity().
@NoArgsConstructor
// Pas de @AllArgsConstructor : vérifié qu'aucun endroit du code n'utilise Role(id, nom) (YAGNI) —
// on l'ajoutera seulement le jour où un vrai besoin apparaît (ex: des tests).
public class Role {

    // Clé primaire de la table
    @Id
    // La valeur est générée automatiquement par la base (auto-increment)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Colonne "nom" : obligatoire (nullable = false) et unique (deux rôles ne peuvent pas avoir le même nom)
    @Column(nullable = false, unique = true)
    private String nom; // ex: "ADMIN", "MEMBRE", "TRESORIER"
}
