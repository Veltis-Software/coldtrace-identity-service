package com.acme.coldtrace.platform.iam.interfaces.rest.transform;

import com.acme.coldtrace.platform.iam.domain.model.commands.SocialOrganizationSignUpCommand;
import com.acme.coldtrace.platform.iam.domain.model.valueobjects.SocialProvider;
import com.acme.coldtrace.platform.iam.interfaces.rest.resources.SocialOrganizationSignUpResource;

/**
 * Interface layer translator converting social organization sign-up resources to commands.
 *
 * @since 1.0
 */
public final class SocialOrganizationSignUpCommandFromResourceAssembler {
    private SocialOrganizationSignUpCommandFromResourceAssembler() {
    }

    /**
     * Converts a SocialOrganizationSignUpResource to a SocialOrganizationSignUpCommand.
     *
     * @param provider provider route code
     * @param resource social organization sign-up request resource
     * @return social organization sign-up command
     */
    public static SocialOrganizationSignUpCommand toCommandFromResource(
            String provider,
            SocialOrganizationSignUpResource resource
    ) {
        return new SocialOrganizationSignUpCommand(
                SocialProvider.fromCode(provider),
                resource.idToken(),
                resource.authorizationCode(),
                resource.redirectUri(),
                resource.nonce(),
                resource.organizationName(),
                resource.fullName()
        );
    }
}
