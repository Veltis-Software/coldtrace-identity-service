package com.acme.coldtrace.platform.iam.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Request resource used to authenticate an existing user.
 *
 * @param email user email address
 * @param password raw password
 * @since 1.0
 */
@Schema(
        name = "SignInRequest",
        description = "User sign-in request with credentials",
        example = """
                {
                  "email": "operator@coldtrace.test",
                  "password": "ColdTrace123"
                }
                """
)
public record SignInResource(
        @NotBlank(message = "is required")
        @Email(message = "must be a valid email")
        @Schema(description = "User email address", example = "david@coldtrace.example")
        String email,

        @NotBlank(message = "is required")
        @Schema(description = "User password", example = "ColdTrace123")
        String password
) {
}
