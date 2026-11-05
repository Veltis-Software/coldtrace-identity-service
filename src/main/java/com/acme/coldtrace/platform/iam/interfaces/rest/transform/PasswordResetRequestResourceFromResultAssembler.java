package com.acme.coldtrace.platform.iam.interfaces.rest.transform;

import com.acme.coldtrace.platform.iam.application.commandservices.PasswordResetRequestCommandResult;
import com.acme.coldtrace.platform.iam.interfaces.rest.resources.PasswordResetRequestResource;

/**
 * Interface layer translator converting password reset request results to resources.
 *
 * @since 1.0
 */
public final class PasswordResetRequestResourceFromResultAssembler {
    private PasswordResetRequestResourceFromResultAssembler() {
    }

    public static PasswordResetRequestResource toResourceFromResult(PasswordResetRequestCommandResult result) {
        return new PasswordResetRequestResource(
                result.accepted(),
                result.requestedAt(),
                result.expiresAt(),
                result.deliveryStatus()
        );
    }
}
