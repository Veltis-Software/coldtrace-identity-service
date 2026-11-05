package com.acme.coldtrace.platform.iam.infrastructure.persistence.jpa.entities;

import com.acme.coldtrace.platform.iam.domain.model.valueobjects.RoleName;
import com.acme.coldtrace.platform.iam.infrastructure.persistence.jpa.converters.RoleNamePersistenceConverter;
import com.acme.coldtrace.platform.iam.infrastructure.persistence.jpa.embeddables.PermissionPersistenceEmbeddable;
import com.acme.coldtrace.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * JPA persistence entity for roles and permission metadata.
 *
 * @since 1.0
 */
@Getter
@Setter
@Entity
@Table(name = "roles")
public class RolePersistenceEntity extends AuditableAbstractPersistenceEntity {
    @Convert(converter = RoleNamePersistenceConverter.class)
    @Column(nullable = false, unique = true)
    private RoleName name;

    @Column(nullable = false)
    private String label;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "role_permissions", joinColumns = @JoinColumn(name = "role_id"))
    private List<PermissionPersistenceEmbeddable> permissions = new ArrayList<>();
}
