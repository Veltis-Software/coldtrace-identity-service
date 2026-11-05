package com.acme.coldtrace.platform.iam.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Request resource for social authentication token exchange.
 *
 * @param idToken provider identity token
 * @param authorizationCode provider authorization code
 * @param redirectUri redirect URI used by the OAuth flow
 * @param nonce optional nonce expected in the identity token
 * @since 1.0
 */
@Schema(
        name = "SocialTokenExchangeRequest",
        description = "Social authentication payload containing either an ID token or an authorization code",
        example = """
                {
                  "idToken": "eyJhbGciOiJSUzI1NiJ9...",
                  "authorizationCode": null,
                  "redirectUri": "https://coldtrace-frontend-liard.vercel.app/identity-access/sign-in",
                  "nonce": "nonce-123"
                }
                """
)
public record SocialTokenExchangeResource(
        @Schema(description = "Provider ID token returned by Google or Apple", example = "eyJhbGciOiJSUzI1NiJ9...")
        String idToken,

        @Schema(description = "Provider authorization code to exchange server-side", example = "4/0AbUR2VN...")
        String authorizationCode,

        @Schema(description = "Redirect URI used in the OAuth authorization request", example = "http://localhost:4200")
        String redirectUri,

        @Schema(description = "Nonce expected in the provider ID token", example = "9c4c9b7d...")
        String nonce
) {
}
