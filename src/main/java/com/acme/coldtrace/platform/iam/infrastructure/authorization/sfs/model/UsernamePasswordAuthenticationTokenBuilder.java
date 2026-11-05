package com.acme.coldtrace.platform.iam.infrastructure.authorization.sfs.model;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

/**
 * Factory for Spring Security username/password authentication tokens.
 *
 * @since 1.0
 */
public final class UsernamePasswordAuthenticationTokenBuilder {
    private UsernamePasswordAuthenticationTokenBuilder() {
    }

    public static UsernamePasswordAuthenticationToken build(UserDetails principal, HttpServletRequest request) {
        var authentication = new UsernamePasswordAuthenticationToken(
                principal,
                null,
                principal.getAuthorities()
        );
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        return authentication;
    }
}
