package com.acme.coldtrace.platform.iam.interfaces.rest.transform;

import com.acme.coldtrace.platform.iam.application.commandservices.SocialIdentityProfileCommandResult;
import com.acme.coldtrace.platform.iam.interfaces.rest.resources.SocialIdentityProfileResource;

/**
 * Interface layer translator for social identity profile preview resources.
 *
 * @since 1.0
 */
public final class SocialIdentityProfileResourceFromResultAssembler {
    private SocialIdentityProfileResourceFromResultAssembler() {
    }

    public static SocialIdentityProfileResource toResourceFromResult(SocialIdentityProfileCommandResult result) {
        return new SocialIdentityProfileResource(result.idToken(), result.email(), result.fullName());
    }
}
