package com.acme.coldtrace.platform.iam.application.commandservices;

import com.acme.coldtrace.platform.iam.domain.model.commands.CreateOrganizationSignUpCommand;
import com.acme.coldtrace.platform.shared.application.result.Result;

/**
 * Application service contract for organization sign-up command operations.
 *
 * @since 1.0
 */
public interface OrganizationSignUpCommandService {
    /**
     * Handles organization sign-up by creating the organization and first user in one transaction.
     *
     * @param command command containing organization and first-user data
     * @return success with the created organization and user, or failure with a sign-up error
     * @see CreateOrganizationSignUpCommand
     */
    Result<OrganizationSignUpCommandResult, OrganizationSignUpCommandFailure> handle(CreateOrganizationSignUpCommand command);
}
