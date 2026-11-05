package com.acme.coldtrace.platform.iam.interfaces.rest.resources;

/**
 * Response resource representing a user.
 *
 * @param id user identifier
 * @param uuid generated user code
 * @param organizationUserId generated organization user identifier
 * @param firstName user first name
 * @param lastName user last name
 * @param email user email address
 * @param organizationId organization identifier
 * @param roleId role identifier
 * @since 1.0
 */
public record UserResource(
        Long id,
        String uuid,
        Long organizationUserId,
        String firstName,
        String lastName,
        String email,
        Long organizationId,
        Long roleId
) {
}
