package com.acme.coldtrace.platform.iam.application.internal.outboundservices.social;

import com.acme.coldtrace.platform.iam.domain.model.commands.SocialSignInCommand;
import com.acme.coldtrace.platform.shared.application.result.ApplicationError;
import com.acme.coldtrace.platform.shared.application.result.Result;

/**
 * Outbound service contract for validating external provider identity.
 *
 * @since 1.0
 */
public interface ExternalIdentityProviderService {
    /**
     * Validates the social sign-in payload and returns the provider identity.
     *
     * @param command social sign-in command
     * @return verified provider identity or controlled authentication error
     */
    Result<ProviderIdentity, ApplicationError> validate(SocialSignInCommand command);
}
