package com.acme.coldtrace.platform.iam.interfaces.rest.transform;

import com.acme.coldtrace.platform.iam.domain.model.commands.CreateUserCommand;
import com.acme.coldtrace.platform.iam.interfaces.rest.resources.CreateUserResource;

/**
 * Interface layer translator converting user creation resources to commands.
 *
 * @since 1.0
 */
public class CreateUserCommandFromResourceAssembler {
    /**
     * Converts a CreateUserResource to a CreateUserCommand.
     *
     * @param resource user creation request resource
     * @param organizationId organization identifier from the route
     * @return create user command
     */
    public static CreateUserCommand toCommandFromResource(CreateUserResource resource, Long organizationId) {
        return new CreateUserCommand(
                resource.firstName(),
                resource.lastName(),
                resource.email(),
                resource.password(),
                organizationId,
                resource.roleId()
        );
    }
}
