package com.acme.coldtrace.platform.iam.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * Request resource used to create an organization-scoped user.
 * <p>
 * The organization identifier is provided by the route, while this payload carries
 * the user profile fields and the role that should be assigned during creation.
 *
 * @param firstName user first name
 * @param lastName optional user last name
 * @param email user email address
 * @param password password used to enable direct sign-in
 * @param roleId role identifier
 * @since 1.0
 */
@Schema(
        name = "CreateUserRequest",
        description = "Request payload for creating a user inside an organization"
)
public record CreateUserResource(
        @NotBlank(message = "is required")
        @Schema(description = "User first name", example = "Maria")
        String firstName,

        @Schema(description = "Optional user last name", example = "Torres")
        String lastName,

        @NotBlank(message = "is required")
        @Email(message = "must be a valid email")
        @Schema(description = "User email address", example = "maria@coldtrace.test")
        String email,

        @NotBlank(message = "is required")
        @Size(min = 8, message = "must have at least 8 characters")
        @Schema(description = "Password used to create credentials for sign-in", example = "ColdTrace123")
        String password,

        @NotNull(message = "is required")
        @Positive(message = "must be positive")
        @Schema(description = "Role identifier assigned to the user", example = "2")
        Long roleId
) {
}
