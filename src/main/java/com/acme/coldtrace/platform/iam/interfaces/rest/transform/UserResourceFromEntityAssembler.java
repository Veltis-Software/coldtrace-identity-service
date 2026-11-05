package com.acme.coldtrace.platform.iam.interfaces.rest.transform;

import com.acme.coldtrace.platform.iam.domain.model.aggregates.User;
import com.acme.coldtrace.platform.iam.interfaces.rest.resources.UserResource;

/**
 * Interface layer translator converting user aggregates to resources.
 *
 * @since 1.0
 */
public class UserResourceFromEntityAssembler {
    /**
     * Converts a user aggregate to a UserResource.
     *
     * @param entity user aggregate
     * @return user response resource
     */
    public static UserResource toResourceFromEntity(User entity) {
        return new UserResource(
                entity.getId(),
                entity.getUuid() == null ? "USR-" + entity.getId() : entity.getUuid(),
                entity.getOrganizationUserId() == null ? entity.getId() : entity.getOrganizationUserId(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getEmail(),
                entity.getOrganizationId(),
                entity.getRoleId()
        );
    }
}
