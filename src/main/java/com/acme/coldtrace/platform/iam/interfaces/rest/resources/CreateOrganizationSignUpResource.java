package com.acme.coldtrace.platform.iam.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request resource for signing up an organization and its first user.
 *
 * <p>The resource captures the initial onboarding payload owned by the backend:
 * organization commercial data plus the first administrative user profile. It
 * performs request-level validation for required text and email shape before
 * the application layer evaluates duplicates, seeded roles and transactional
 * creation rules.</p>
 *
 * @param legalName organization legal name
 * @param commercialName organization commercial name
 * @param taxId optional tax identifier
 * @param contactEmail organization contact email
 * @param firstName first user first name
 * @param lastName first user last name
 * @param email first user email address
 * @param password first user password
 * @since 1.0
 */
@Schema(
        name = "CreateOrganizationSignUpRequest",
        description = "Request payload for creating an organization and its first user"
)
public record CreateOrganizationSignUpResource(
        @NotBlank(message = "is required")
        @Schema(description = "Registered organization legal name", example = "ColdTrace Logistics S.A.C.")
        String legalName,

        @NotBlank(message = "is required")
        @Schema(description = "Organization commercial name", example = "ColdTrace Logistics")
        String commercialName,

        @Schema(description = "Optional organization tax identifier", example = "20601234567")
        String taxId,

        @NotBlank(message = "is required")
        @Email(message = "must be a valid email address")
        @Schema(description = "Organization contact email", example = "operations@coldtrace.example")
        String contactEmail,

        @NotBlank(message = "is required")
        @Schema(description = "First user given name", example = "David")
        String firstName,

        @Schema(description = "First user family name", example = "Torres")
        String lastName,

        @NotBlank(message = "is required")
        @Email(message = "must be a valid email address")
        @Schema(description = "First user email address", example = "david@coldtrace.example")
        String email,

        @NotBlank(message = "is required")
        @Size(min = 8, message = "must have at least 8 characters")
        @Schema(description = "First user password", example = "ColdTrace123")
        String password
) {
}
