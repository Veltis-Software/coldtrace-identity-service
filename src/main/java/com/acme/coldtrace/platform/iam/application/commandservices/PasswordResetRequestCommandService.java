package com.acme.coldtrace.platform.iam.application.commandservices;

import com.acme.coldtrace.platform.iam.domain.model.commands.CreatePasswordResetRequestCommand;
import com.acme.coldtrace.platform.shared.application.result.ApplicationError;
import com.acme.coldtrace.platform.shared.application.result.Result;

/**
 * Application service contract for password reset request commands.
 *
 * @since 1.0
 */
public interface PasswordResetRequestCommandService {
    /**
     * Handles a password reset request.
     *
     * @param command request command
     * @return accepted response or controlled application error
     */
    Result<PasswordResetRequestCommandResult, ApplicationError> handle(CreatePasswordResetRequestCommand command);
}
