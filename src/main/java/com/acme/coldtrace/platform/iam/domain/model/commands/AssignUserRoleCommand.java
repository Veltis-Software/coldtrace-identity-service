package com.acme.coldtrace.platform.iam.domain.model.commands;

/**
 * Command for assigning a role to an existing user inside an organization.
 * <p>
 * The command keeps the organization identifier together with the user
 * identifier so the application layer can enforce organization membership
 * before changing the role reference. This prevents updates that would assign
 * a role through a route that belongs to a different organization.
 *
 * @param organizationId organization identifier obtained from the route
 * @param userId user identifier obtained from the route
 * @param roleId target role identifier obtained from the request body
 * @since 1.0
 */
public record AssignUserRoleCommand(
        Long organizationId,
        Long userId,
        Long roleId
) {
    /**
     * Validates the identifiers needed to assign the role.
     *
     * @throws IllegalArgumentException if any identifier is null or not positive
     */
    public AssignUserRoleCommand {
        if (organizationId == null || organizationId <= 0) {
            throw new IllegalArgumentException("identity-access.user.error.organizationId.invalid");
        }
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("identity-access.user.error.userId.invalid");
        }
        if (roleId == null || roleId <= 0) {
            throw new IllegalArgumentException("identity-access.user.error.roleId.invalid");
        }
    }
}
