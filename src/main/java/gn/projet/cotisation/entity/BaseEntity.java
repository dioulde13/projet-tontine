package gn.projet.cotisation.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;

import lombok.Getter;
import lombok.Setter;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

// Classe de base pour les entités dont on veut tracer automatiquement la création/modification.
// @MappedSuperclass : PAS une entité à part entière (aucune table "base_entity" créée) — ses champs
// sont simplement ajoutés aux colonnes de la table de chaque sous-classe qui en hérite.
// @EntityListeners(AuditingEntityListener.class) : à chaque @PrePersist/@PreUpdate d'une sous-classe,
// Hibernate appelle cet écouteur Spring Data, qui remplit les 4 champs ci-dessous automatiquement
// (voir JpaAuditingConfig pour l'activation globale, et SpringSecurityAuditorAware pour le "qui").
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public abstract class BaseEntity {

    // Renseigné UNE SEULE FOIS, à la création. updatable = false exclut explicitement cette colonne
    // des UPDATE générés par Hibernate : même si un code futur modifiait ce champ en mémoire par
    // erreur, la date d'origine en base ne serait jamais écrasée.
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // Téléphone (voir CustomUserDetails.getUsername()) de l'utilisateur authentifié à l'origine de la
    // création. Reste NULL si l'enregistrement a été créé hors contexte authentifié (ex: l'inscription
    // publique POST /api/utilisateurs) — voir SpringSecurityAuditorAware, jamais de valeur inventée.
    @CreatedBy
    @Column(name = "created_by", updatable = false)
    private String createdBy;

    // Mis à jour à chaque @PreUpdate (donc aussi à la création, comme createdAt).
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @LastModifiedBy
    @Column(name = "updated_by")
    private String updatedBy;
}
