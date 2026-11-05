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

/**
 * JPA persistence entity for users.
 *
 * @since 1.0
 */
@Getter
@Setter
@Entity
@Table(name = "users")
public class UserPersistenceEntity extends AuditableAbstractPersistenceEntity {
    private String uuid;

    private Long organizationUserId;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Convert(converter = EmailAddressPersistenceConverter.class)
    @Column(nullable = false, unique = true)
    private EmailAddress email;

    @Column(name = "password_hash")
    private String passwordHash;

    @Column(nullable = false)
    private Long organizationId;

    @Column(nullable = false)
    private Long roleId;
}
