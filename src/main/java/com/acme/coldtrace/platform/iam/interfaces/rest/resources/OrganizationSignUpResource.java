package com.acme.coldtrace.platform.iam.interfaces.rest.resources;

/**
 * Response resource for an organization sign-up.
 *
 * @param organization created organization
 * @param user first user created for the organization
 * @since 1.0
 */
public record OrganizationSignUpResource(
        OrganizationResource organization,
        UserResource user
) {
}
