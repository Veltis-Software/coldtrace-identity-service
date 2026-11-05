package com.acme.coldtrace.platform.iam.interfaces.rest.transform;

import com.acme.coldtrace.platform.iam.domain.model.commands.AssignUserRoleCommand;
import com.acme.coldtrace.platform.iam.interfaces.rest.resources.AssignUserRoleResource;

/**
 * Interface layer translator converting role assignment resources to commands.
 *
 * @since 1.0
 */
public class AssignUserRoleCommandFromResourceAssembler {
    /**
     * Converts an AssignUserRoleResource to an AssignUserRoleCommand.
     *
     * @param resource role assignment request resource
     * @param organizationId organization identifier from the route
     * @param userId user identifier from the route
     * @return assign user role command
     */
    public static AssignUserRoleCommand toCommandFromResource(
            AssignUserRoleResource resource,
            Long organizationId,
            Long userId
    ) {
        return new AssignUserRoleCommand(
                organizationId,
                userId,
                resource.roleId()
        );
    }
}
