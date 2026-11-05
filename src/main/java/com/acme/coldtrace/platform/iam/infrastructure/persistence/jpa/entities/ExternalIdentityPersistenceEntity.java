package com.acme.coldtrace.platform.iam.infrastructure.persistence.jpa.entities;

import com.acme.coldtrace.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

/**
 * JPA persistence entity for external identity links.
 *
 * @since 1.0
 */
@Getter
@Setter
@Entity
@Table(
        name = "external_identities",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_external_identities_provider_subject",
                        columnNames = {"provider", "provider_subject"}
                )
        }
)
public class ExternalIdentityPersistenceEntity extends AuditableAbstractPersistenceEntity {
    @Column(nullable = false, length = 32)
    private String provider;

    @Column(name = "provider_subject", nullable = false, length = 255)
    private String providerSubject;

    @Column(length = 255)
    private String email;

    @Column(nullable = false)
    private Long userId;
}
