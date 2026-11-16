package gn.projet.cotisation.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

// Active l'audit automatique de Spring Data JPA (createdAt/createdBy/updatedAt/updatedBy — voir
// entity.BaseEntity) pour toute l'application. Un seul bean AuditorAware<String> existe dans le
// contexte (SpringSecurityAuditorAware) : pas besoin de préciser auditorAwareRef explicitement,
// Spring Data le trouve seul.
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
}
