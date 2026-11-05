package com.acme.coldtrace.platform.iam.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Request resource used to request a password reset.
 *
 * @param email user email address
 * @since 1.0
 */
@Schema(
        name = "CreatePasswordResetRequest",
        description = "Password reset request payload"
)
public record CreatePasswordResetRequestResource(
        @NotBlank(message = "is required")
        @Email(message = "must be a valid email")
        @Schema(description = "User email address", example = "david@coldtrace.example")
        String email
) {
}
