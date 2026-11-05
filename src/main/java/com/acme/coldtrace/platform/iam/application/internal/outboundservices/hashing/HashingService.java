package com.acme.coldtrace.platform.iam.application.internal.outboundservices.hashing;

/**
 * Outbound port for password hashing operations required by IAM.
 *
 * @since 1.0
 */
public interface HashingService {
    /**
     * Encodes a raw password for persistence.
     *
     * @param rawPassword raw password value
     * @return encoded password representation
     */
    String encode(CharSequence rawPassword);

    /**
     * Verifies whether a raw password matches an encoded password.
     *
     * @param rawPassword raw password value
     * @param encodedPassword encoded password representation
     * @return true when both values match
     */
    boolean matches(CharSequence rawPassword, String encodedPassword);
}
