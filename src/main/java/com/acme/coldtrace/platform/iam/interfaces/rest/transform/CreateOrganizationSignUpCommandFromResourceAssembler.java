package com.acme.coldtrace.platform.iam.interfaces.rest.transform;

import com.acme.coldtrace.platform.iam.domain.model.commands.CreateOrganizationSignUpCommand;
import com.acme.coldtrace.platform.iam.interfaces.rest.resources.CreateOrganizationSignUpResource;

/**
 * Interface layer translator converting organization sign-up resources to commands.
 *
 * @since 1.0
 */
public class CreateOrganizationSignUpCommandFromResourceAssembler {
    /**
     * Converts a CreateOrganizationSignUpResource to a CreateOrganizationSignUpCommand.
     *
     * @param resource organization sign-up request resource
     * @return create organization sign-up command
     */
    public static CreateOrganizationSignUpCommand toCommandFromResource(CreateOrganizationSignUpResource resource) {
        return new CreateOrganizationSignUpCommand(
                resource.legalName(),
                resource.commercialName(),
                resource.taxId(),
                resource.contactEmail(),
                resource.firstName(),
                resource.lastName(),
                resource.email(),
                resource.password()
        );
    }
}
