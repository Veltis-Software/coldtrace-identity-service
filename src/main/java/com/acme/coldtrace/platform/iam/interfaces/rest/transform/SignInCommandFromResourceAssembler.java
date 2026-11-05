package com.acme.coldtrace.platform.iam.interfaces.rest.transform;

import com.acme.coldtrace.platform.iam.domain.model.commands.SignInCommand;
import com.acme.coldtrace.platform.iam.interfaces.rest.resources.SignInResource;

/**
 * Interface layer translator converting sign-in resources to commands.
 *
 * @since 1.0
 */
public final class SignInCommandFromResourceAssembler {
    private SignInCommandFromResourceAssembler() {
    }

    public static SignInCommand toCommandFromResource(SignInResource resource) {
        return new SignInCommand(resource.email(), resource.password());
    }
}
