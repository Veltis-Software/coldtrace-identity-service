package com.acme.coldtrace.platform.iam.domain.model.commands;

import com.acme.coldtrace.platform.iam.domain.model.valueobjects.SocialProvider;

/**
 * Command for creating the first organization account through a social provider.
 *
 * @param provider social provider code
 * @param idToken identity token returned by the provider
 * @param authorizationCode authorization code returned by the provider
 * @param redirectUri redirect URI used by the frontend OAuth flow
 * @param nonce optional nonce expected in the identity token
 * @param organizationName organization legal and commercial name
 * @param fullName first user full name
 * @since 1.0
 */
public record SocialOrganizationSignUpCommand(
        SocialProvider provider,
        String idToken,
        String authorizationCode,
        String redirectUri,
        String nonce,
        String organizationName,
        String fullName
) {
    /**
     * Validates and normalizes the social organization sign-up payload.
     *
     * @throws IllegalArgumentException if required fields are missing
     */
    public SocialOrganizationSignUpCommand {
        if (provider == null) {
            throw new IllegalArgumentException("identity-access.authentication.error.provider.required");
        }
        if (!hasText(idToken) && !hasText(authorizationCode)) {
            throw new IllegalArgumentException("identity-access.authentication.error.social-token.required");
        }
        idToken = normalizeNullable(idToken);
        authorizationCode = normalizeNullable(authorizationCode);
        redirectUri = normalizeNullable(redirectUri);
        nonce = normalizeNullable(nonce);
        organizationName = requireNonBlank(
                organizationName,
                "identity-access.organization.error.commercialName.required"
        );
        fullName = requireNonBlank(fullName, "identity-access.user.error.firstName.required");
    }

    /**
     * Converts this command to the provider-validation command already used by sign-in.
     *
     * @return social sign-in command carrying provider token data
     */
    public SocialSignInCommand toSocialSignInCommand() {
        return new SocialSignInCommand(provider, idToken, authorizationCode, redirectUri, nonce);
    }

    private static String requireNonBlank(String value, String messageKey) {
        if (!hasText(value)) {
            throw new IllegalArgumentException(messageKey);
        }
        return value.trim();
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private static String normalizeNullable(String value) {
        return hasText(value) ? value.trim() : null;
    }
}
