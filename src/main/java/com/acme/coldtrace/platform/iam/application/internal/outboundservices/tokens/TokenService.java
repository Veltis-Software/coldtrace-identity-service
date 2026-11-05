package com.acme.coldtrace.platform.iam.application.internal.outboundservices.tokens;

/**
 * Outbound port for bearer token issuance and validation.
 *
 * @since 1.0
 */
public interface TokenService {
    /**
     * Generates a signed token for a user email.
     *
     * @param email principal email
     * @return signed token value
     */
    String generateToken(String email);

    /**
     * Extracts the user email from a token.
     *
     * @param token token value
     * @return email embedded in the token
     */
    String getEmailFromToken(String token);

    /**
     * Validates a token.
     *
     * @param token token value
     * @return true when token is valid
     */
    boolean validateToken(String token);
}
