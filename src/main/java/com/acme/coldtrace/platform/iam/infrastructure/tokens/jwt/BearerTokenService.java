package com.acme.coldtrace.platform.iam.infrastructure.tokens.jwt;

import com.acme.coldtrace.platform.iam.application.internal.outboundservices.tokens.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;

/**
 * Marker interface for JWT bearer token operations.
 *
 * @since 1.0
 */
public interface BearerTokenService extends TokenService {
    /**
     * Extracts a bearer token from an HTTP request.
     *
     * @param request HTTP request
     * @return token value or null
     */
    String getBearerTokenFrom(HttpServletRequest request);

    /**
     * Generates a token from Spring Security authentication.
     *
     * @param authentication authentication object
     * @return token value
     */
    String generateToken(Authentication authentication);
}
