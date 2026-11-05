package com.acme.coldtrace.platform.iam.infrastructure.persistence.jpa.entities;

import com.acme.coldtrace.platform.iam.domain.model.valueobjects.EmailAddress;
import com.acme.coldtrace.platform.iam.infrastructure.persistence.jpa.converters.EmailAddressPersistenceConverter;
import com.acme.coldtrace.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/**
 * JPA persistence entity for password reset requests.
 *
 * @since 1.0
 */
@Getter
@Setter
@Entity
@Table(name = "password_reset_requests")
public class PasswordResetRequestPersistenceEntity extends AuditableAbstractPersistenceEntity {
    @Convert(converter = EmailAddressPersistenceConverter.class)
    @Column(nullable = false)
    private EmailAddress email;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, unique = true, length = 128)
    private String tokenHash;

    @Column(nullable = false)
    private Instant requestedAt;

    @Column(nullable = false)
    private Instant expiresAt;

    private Instant consumedAt;
}
