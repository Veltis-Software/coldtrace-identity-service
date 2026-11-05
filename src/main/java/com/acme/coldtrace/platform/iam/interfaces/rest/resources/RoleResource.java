package com.acme.coldtrace.platform.iam.interfaces.rest.resources;

import java.util.List;

/**
 * Response resource representing a role and its permissions.
 *
 * @param id role identifier
 * @param name stable role name
 * @param label role display label
 * @param permissions permissions assigned to the role
 * @since 1.0
 */
public record RoleResource(
        Long id,
        String name,
        String label,
        List<PermissionResource> permissions
) {
}
