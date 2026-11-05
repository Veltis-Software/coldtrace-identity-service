package com.acme.coldtrace.platform.iam.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Response resource for a validated social profile preview.
 *
 * @param idToken provider ID token verified by the application
 * @param email verified provider email
 * @param fullName suggested full name
 * @since 1.0
 */
@Schema(
        name = "SocialIdentityProfile",
        description = "Validated social identity profile used to prefill organization onboarding"
)
public record SocialIdentityProfileResource(
        @Schema(description = "Provider ID token verified by the backend", example = "eyJhbGciOiJSUzI1NiJ9...")
        String idToken,

        @Schema(description = "Verified provider email", example = "david@coldtrace.example")
        String email,

        @Schema(description = "Suggested full name", example = "David Chen")
        String fullName
) {
}
