package com.acme.coldtrace.platform.iam.application.commandservices;

import com.acme.coldtrace.platform.iam.domain.model.commands.SocialOrganizationSignUpCommand;
import com.acme.coldtrace.platform.shared.application.result.ApplicationError;
import com.acme.coldtrace.platform.shared.application.result.Result;

/**
 * Application service contract for social organization sign-up commands.
 *
 * @since 1.0
 */
public interface SocialOrganizationSignUpCommandService {
    /**
     * Handles social provider organization sign-up.
     *
     * @param command command containing provider token and organization profile data
     * @return success with authenticated user and token or controlled application error
     */
    Result<AuthenticatedUserCommandResult, ApplicationError> handle(SocialOrganizationSignUpCommand command);
}
