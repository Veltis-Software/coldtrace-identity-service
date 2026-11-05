package com.acme.coldtrace.platform.iam.interfaces.rest.transform;

import com.acme.coldtrace.platform.iam.domain.model.commands.SocialSignInCommand;
import com.acme.coldtrace.platform.iam.domain.model.valueobjects.SocialProvider;
import com.acme.coldtrace.platform.iam.interfaces.rest.resources.SocialTokenExchangeResource;

/**
 * Interface layer translator converting social token exchange resources to commands.
 *
 * @since 1.0
 */
public final class SocialSignInCommandFromResourceAssembler {
    private SocialSignInCommandFromResourceAssembler() {
    }

    public static SocialSignInCommand toCommandFromResource(
            String provider,
            SocialTokenExchangeResource resource
    ) {
        if (resource == null) {
            throw new IllegalArgumentException("identity-access.authentication.error.social-token.required");
        }
        return new SocialSignInCommand(
                SocialProvider.fromCode(provider),
                resource.idToken(),
                resource.authorizationCode(),
                resource.redirectUri(),
                resource.nonce()
        );
    }
}
