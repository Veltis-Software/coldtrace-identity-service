package com.acme.coldtrace.platform.iam.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

/**
 * Response resource returned after a password reset request is accepted.
 *
 * @param accepted whether the request was accepted
 * @param requestedAt acceptance timestamp
 * @param expiresAt request expiration timestamp
 * @param deliveryStatus delivery state for the academic flow
 * @since 1.0
 */
@Schema(
        name = "PasswordResetRequest",
        description = "Password reset request acceptance response"
)
public record PasswordResetRequestResource(
        @Schema(description = "Whether the request was accepted", example = "true")
        boolean accepted,

        @Schema(description = "Timestamp when the request was accepted")
        Instant requestedAt,

        @Schema(description = "Timestamp when the reset request expires")
        Instant expiresAt,

        @Schema(description = "Delivery state for the reset email integration", example = "EMAIL_DELIVERY_NOT_CONFIGURED")
        String deliveryStatus
) {
}
