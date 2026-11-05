package com.acme.coldtrace.platform.iam.application.internal.outboundservices.social;

import com.acme.coldtrace.platform.iam.domain.model.valueobjects.SocialProvider;

/**
 * Verified identity returned by a social provider.
 *
 * @param provider external provider
 * @param subject stable provider subject
 * @param email verified email when the provider supplies one
 * @param fullName profile display name when the provider supplies one
 * @param givenName profile given name when the provider supplies one
 * @param familyName profile family name when the provider supplies one
 * @param idToken provider ID token verified by the application
 * @since 1.0
 */
public record ProviderIdentity(
        SocialProvider provider,
        String subject,
        String email,
        String fullName,
        String givenName,
        String familyName,
        String idToken
) {
}
