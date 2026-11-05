package com.acme.coldtrace.platform.iam.infrastructure.persistence.jpa.assemblers;

import com.acme.coldtrace.platform.iam.domain.model.entities.Role;
import com.acme.coldtrace.platform.iam.domain.model.valueobjects.Permission;
import com.acme.coldtrace.platform.iam.infrastructure.persistence.jpa.embeddables.PermissionPersistenceEmbeddable;
import com.acme.coldtrace.platform.iam.infrastructure.persistence.jpa.entities.RolePersistenceEntity;

/**
 * Assembler that translates roles between domain and persistence models.
 *
 * @since 1.0
 */
public final class RolePersistenceAssembler {
    private RolePersistenceAssembler() {
    }

    private static Permission toDomainPermission(PermissionPersistenceEmbeddable value) {
        return new Permission(value.getId(), value.getResource(), value.getAction(), value.getDescription());
    }

    private static PermissionPersistenceEmbeddable toPersistencePermission(Permission value) {
        return new PermissionPersistenceEmbeddable(
                value.getId(),
                value.getResource(),
                value.getAction(),
                value.getDescription()
        );
    }

    public static Role toDomainFromPersistence(RolePersistenceEntity entity) {
        return new Role(
                entity.getId(),
                entity.getName(),
                entity.getLabel(),
                entity.getPermissions().stream().map(RolePersistenceAssembler::toDomainPermission).toList()
        );
    }

    public static RolePersistenceEntity toPersistenceFromDomain(Role role) {
        var entity = new RolePersistenceEntity();
        entity.setId(role.getId());
        copyDomainState(role, entity);
        return entity;
    }

    public static void copyDomainState(Role role, RolePersistenceEntity entity) {
        entity.setName(role.getNameValue());
        entity.setLabel(role.getLabel());
        entity.setPermissions(role.getPermissions().stream()
                .map(RolePersistenceAssembler::toPersistencePermission)
                .toList());
    }
}
