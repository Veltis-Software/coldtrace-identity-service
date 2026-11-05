package com.acme.coldtrace.platform.iam.domain.model.commands;

import com.acme.coldtrace.platform.iam.domain.model.valueobjects.SocialProvider;

/**
 * Command for authenticating a user through an external OIDC provider.
 *
 * @param provider social provider code
 * @param idToken identity token returned by the provider
 * @param authorizationCode authorization code returned by the provider
 * @param redirectUri redirect URI used by the frontend OAuth flow
 * @param nonce optional nonce expected in the identity token
 * @since 1.0
 */
public record SocialSignInCommand(
        SocialProvider provider,
        String idToken,
        String authorizationCode,
        String redirectUri,
        String nonce
) {
    public SocialSignInCommand {
        if (!hasText(idToken) && !hasText(authorizationCode)) {
            throw new IllegalArgumentException("identity-access.authentication.error.social-token.required");
        }
        idToken = normalizeNullable(idToken);
        authorizationCode = normalizeNullable(authorizationCode);
        redirectUri = normalizeNullable(redirectUri);
        nonce = normalizeNullable(nonce);
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private static String normalizeNullable(String value) {
        return hasText(value) ? value.trim() : null;
    }
}
