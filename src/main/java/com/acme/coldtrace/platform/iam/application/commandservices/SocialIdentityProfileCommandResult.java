package com.acme.coldtrace.platform.iam.application.commandservices;

/**
 * Validated social profile data used to prefill organization onboarding.
 *
 * @param idToken provider ID token verified by the application
 * @param email verified provider email
 * @param fullName suggested full name
 * @since 1.0
 */
public record SocialIdentityProfileCommandResult(
        String idToken,
        String email,
        String fullName
) {
}
