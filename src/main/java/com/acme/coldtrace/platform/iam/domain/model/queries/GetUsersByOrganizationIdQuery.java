package com.acme.coldtrace.platform.iam.domain.model.queries;

/**
 * Query for retrieving users that belong to an organization.
 *
 * @param organizationId organization identifier used to filter users
 * @since 1.0
 */
public record GetUsersByOrganizationIdQuery(Long organizationId) {
    public GetUsersByOrganizationIdQuery {
        if (organizationId == null || organizationId <= 0)
            throw new IllegalArgumentException("identity-access.user.error.organizationId.invalid");
    }
}
