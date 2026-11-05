package com.acme.coldtrace.platform.iam.interfaces.rest.transform;

import com.acme.coldtrace.platform.iam.domain.model.commands.CreatePasswordResetRequestCommand;
import com.acme.coldtrace.platform.iam.interfaces.rest.resources.CreatePasswordResetRequestResource;

/**
 * Interface layer translator converting password reset request resources to commands.
 *
 * @since 1.0
 */
public final class CreatePasswordResetRequestCommandFromResourceAssembler {
    private CreatePasswordResetRequestCommandFromResourceAssembler() {
    }

    public static CreatePasswordResetRequestCommand toCommandFromResource(
            CreatePasswordResetRequestResource resource
    ) {
        return new CreatePasswordResetRequestCommand(resource.email());
    }
}
