package gn.projet.cotisation.entity;

// Annotations JPA (jakarta.persistence) : elles décrivent comment cette classe correspond à une table SQL
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne; // relation plusieurs-à-un (plusieurs membres -> un seul village)
import jakarta.persistence.Table;

// Annotations Lombok : génèrent automatiquement du code répétitif (getters, setters, constructeurs)
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Types Java standard pour représenter une collection de rôles sans doublon
import java.util.HashSet;
import java.util.Set;

// Dit à Hibernate/JPA que cette classe doit être mappée sur une table de la base de données
@Entity
// Nom exact de la table en base ("utilisateurs" plutôt que "utilisateur", convention au pluriel)
@Table(name = "utilisateurs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Utilisateur {

    // Clé primaire de la table
    @Id
    // La valeur est générée automatiquement par la base (auto-increment)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nom de famille de l'utilisateur, obligatoire
    @Column(nullable = false)
    private String nom;

    // Prénom de l'utilisateur, obligatoire
    @Column(nullable = false)
    private String prenom;

    // Téléphone : obligatoire et unique (sert d'identifiant de connexion, deux comptes ne peuvent pas le partager)
    @Column(nullable = false, unique = true)
    private String telephone;

    // Mot de passe (idéalement stocké déjà haché, jamais en clair, mais ce n'est pas géré ici)
    @Column(nullable = false)
    private String motDePasse;

    // Relation plusieurs-à-plusieurs : un utilisateur peut avoir plusieurs rôles,
    // et un même rôle peut être attribué à plusieurs utilisateurs.
    // fetch = EAGER : les rôles sont chargés automatiquement en même temps que l'utilisateur
    // (pratique ici car la liste des rôles est petite et souvent utile, ex: pour la sécurité).
    @ManyToMany(fetch = FetchType.EAGER)
    // Décrit la table intermédiaire ("table de jointure") qui stocke les associations utilisateur <-> rôle
    @JoinTable(
            name = "utilisateur_roles", // nom de la table de jointure créée en base
            joinColumns = @JoinColumn(name = "utilisateur_id"), // colonne pointant vers cet Utilisateur
            inverseJoinColumns = @JoinColumn(name = "role_id")  // colonne pointant vers le Role associé
    )
    // HashSet évite les doublons de rôles et initialise la collection pour ne jamais avoir null
    private Set<Role> roles = new HashSet<>();

    // Relation plusieurs-à-plusieurs : rempli UNIQUEMENT si cet utilisateur a le rôle PRESIDENT.
    // Un président peut gérer plusieurs villages ; un village peut (par souplesse) avoir plusieurs présidents.
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "president_villages", // table de jointure dédiée à CETTE relation (différente de utilisateur_roles)
            joinColumns = @JoinColumn(name = "utilisateur_id"),   // colonne pointant vers ce Utilisateur (le président)
            inverseJoinColumns = @JoinColumn(name = "village_id") // colonne pointant vers le Village géré
    )
    private Set<Village> villagesGeres = new HashSet<>();

    // Relation plusieurs-à-un : rempli UNIQUEMENT si cet utilisateur a le rôle MEMBRE.
    // Contrairement à villagesGeres (plusieurs villages possibles), un membre appartient à UN SEUL village
    // à la fois — d'où @ManyToOne (et non @ManyToMany) et pas de table de jointure, juste une colonne
    // "village_id" directement dans la table utilisateurs.
    // nullable = true (implicite) : un ADMIN ou un PRESIDENT n'a pas de village d'appartenance.
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "village_id") // nom de la colonne de clé étrangère créée dans la table "utilisateurs"
    private Village village;
}
