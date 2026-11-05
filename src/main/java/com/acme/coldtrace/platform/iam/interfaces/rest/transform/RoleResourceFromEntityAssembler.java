package com.acme.coldtrace.platform.iam.interfaces.rest.transform;

import com.acme.coldtrace.platform.iam.domain.model.entities.Role;
import com.acme.coldtrace.platform.iam.domain.model.valueobjects.Permission;
import com.acme.coldtrace.platform.iam.interfaces.rest.resources.PermissionResource;
import com.acme.coldtrace.platform.iam.interfaces.rest.resources.RoleResource;

/**
 * Interface layer translator converting role entitys to resources.
 *
 * @since 1.0
 */
public class RoleResourceFromEntityAssembler {
    /**
     * Converts a role entity to a RoleResource.
     *
     * @param entity role entity
     * @return role response resource
     */
    public static RoleResource toResourceFromEntity(Role entity) {
        return new RoleResource(
                entity.getId(),
                entity.getName(),
                entity.getLabel(),
                entity.getPermissions().stream()
                        .map(RoleResourceFromEntityAssembler::toPermissionResourceFromValueObject)
                        .toList()
        );
    }

    /**
     * Converts a permission value object to a PermissionResource.
     *
     * @param permission permission value object
     * @return permission response resource
     */
    private static PermissionResource toPermissionResourceFromValueObject(Permission permission) {
        return new PermissionResource(
                permission.getId(),
                permission.getResource(),
                permission.getAction(),
                permission.getDescription()
        );
    }
}
