package com.acme.coldtrace.platform.iam.application.commandservices;

import java.time.Instant;

/**
 * Application result returned after a password reset request is accepted.
 *
 * @param accepted whether the request was accepted
 * @param requestedAt acceptance timestamp
 * @param expiresAt reset request expiration timestamp
 * @param deliveryStatus delivery state for the academic flow
 * @since 1.0
 */
public record PasswordResetRequestCommandResult(
        boolean accepted,
        Instant requestedAt,
        Instant expiresAt,
        String deliveryStatus
) {
}
