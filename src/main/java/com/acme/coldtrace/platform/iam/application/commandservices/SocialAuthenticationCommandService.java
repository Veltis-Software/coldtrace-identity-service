package com.acme.coldtrace.platform.iam.application.commandservices;

import com.acme.coldtrace.platform.iam.domain.model.commands.SocialSignInCommand;
import com.acme.coldtrace.platform.shared.application.result.ApplicationError;
import com.acme.coldtrace.platform.shared.application.result.Result;

/**
 * Application service contract for social authentication commands.
 *
 * @since 1.0
 */
public interface SocialAuthenticationCommandService {
    /**
     * Handles social authentication through a supported provider.
     *
     * @param command social sign-in command
     * @return success with authenticated user and token or controlled application error
     */
    Result<AuthenticatedUserCommandResult, ApplicationError> handle(SocialSignInCommand command);
}
