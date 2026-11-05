package com.acme.coldtrace.platform.iam.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * Request resource for signing up an organization through a social provider.
 *
 * @param idToken identity token returned by the provider
 * @param authorizationCode authorization code returned by the provider
 * @param redirectUri redirect URI used by the frontend OAuth flow
 * @param nonce optional nonce expected in the identity token
 * @param organizationName organization legal and commercial name
 * @param fullName first user full name
 * @since 1.0
 */
@Schema(
        name = "SocialOrganizationSignUpRequest",
        description = "Request payload for creating an organization and first user through a social provider",
        example = """
                {
                  "idToken": "eyJhbGciOiJSUzI1NiIsImtpZCI6IjEifQ...",
                  "authorizationCode": null,
                  "redirectUri": "https://coldtrace-frontend-liard.vercel.app/identity-access/sign-up",
                  "nonce": "nonce-123",
                  "organizationName": "ColdTrace Market",
                  "fullName": "Jane Smith"
                }
                """
)
public record SocialOrganizationSignUpResource(
        @Schema(description = "Provider ID token", example = "eyJhbGciOiJSUzI1NiIsImtpZCI6IjEifQ...")
        String idToken,

        @Schema(description = "Provider authorization code", example = "4/0AfJohXn...")
        String authorizationCode,

        @Schema(description = "Redirect URI used during authorization-code flow",
                example = "https://coldtrace-frontend-liard.vercel.app/identity-access/sign-up")
        String redirectUri,

        @Schema(description = "Optional nonce expected in the ID token", example = "nonce-123")
        String nonce,

        @NotBlank(message = "is required")
        @Schema(description = "Organization name", example = "ColdTrace Market")
        String organizationName,

        @NotBlank(message = "is required")
        @Schema(description = "First user full name", example = "Jane Smith")
        String fullName
) {
}
