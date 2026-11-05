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
 * JPA persistence entity for organizations.
 *
 * @since 1.0
 */
@Getter
@Setter
@Entity
@Table(name = "organizations")
public class OrganizationPersistenceEntity extends AuditableAbstractPersistenceEntity {
    @Column(nullable = false)
    private String legalName;

    @Column(nullable = false)
    private String commercialName;

    private String taxId;

    @Convert(converter = EmailAddressPersistenceConverter.class)
    @Column(nullable = false)
    private EmailAddress contactEmail;
}
