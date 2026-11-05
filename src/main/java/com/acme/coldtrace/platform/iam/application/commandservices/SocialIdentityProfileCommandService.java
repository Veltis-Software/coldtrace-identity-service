package com.acme.coldtrace.platform.iam.application.commandservices;

import com.acme.coldtrace.platform.iam.domain.model.commands.SocialSignInCommand;
import com.acme.coldtrace.platform.shared.application.result.ApplicationError;
import com.acme.coldtrace.platform.shared.application.result.Result;

/**
 * Application service for validating a social identity before organization onboarding.
 *
 * @since 1.0
 */
public interface SocialIdentityProfileCommandService {
    /**
     * Validates the provider payload and returns profile data usable by the onboarding form.
     *
     * @param command social provider command
     * @return provider profile preview or controlled validation error
     */
    Result<SocialIdentityProfileCommandResult, ApplicationError> handle(SocialSignInCommand command);
}
