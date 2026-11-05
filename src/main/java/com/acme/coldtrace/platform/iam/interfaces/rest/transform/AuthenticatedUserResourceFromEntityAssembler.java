package com.acme.coldtrace.platform.iam.interfaces.rest.transform;

import com.acme.coldtrace.platform.iam.domain.model.aggregates.User;
import com.acme.coldtrace.platform.iam.interfaces.rest.resources.AuthenticatedUserResource;

/**
 * Interface layer translator converting authentication results to resources.
 *
 * @since 1.0
 */
public final class AuthenticatedUserResourceFromEntityAssembler {
    private AuthenticatedUserResourceFromEntityAssembler() {
    }

    public static AuthenticatedUserResource toResourceFromEntity(User user, String token) {
        return new AuthenticatedUserResource(
                user.getId(),
                user.getUuid(),
                user.getOrganizationUserId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getOrganizationId(),
                user.getRoleId(),
                token
        );
    }
}
