package com.acme.coldtrace.platform.iam.infrastructure.authorization.sfs.pipeline;

import com.acme.coldtrace.platform.iam.infrastructure.authorization.sfs.model.UsernamePasswordAuthenticationTokenBuilder;
import com.acme.coldtrace.platform.iam.infrastructure.tokens.jwt.BearerTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Servlet filter that loads Spring Security authentication from a bearer token.
 *
 * @since 1.0
 */
@Slf4j
public class BearerAuthorizationRequestFilter extends OncePerRequestFilter {
    private final BearerTokenService tokenService;
    private final UserDetailsService userDetailsService;

    public BearerAuthorizationRequestFilter(BearerTokenService tokenService, UserDetailsService userDetailsService) {
        this.tokenService = tokenService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            var token = tokenService.getBearerTokenFrom(request);
            if (token != null && tokenService.validateToken(token)) {
                var email = tokenService.getEmailFromToken(token);
                var userDetails = userDetailsService.loadUserByUsername(email);
                SecurityContextHolder.getContext().setAuthentication(
                        UsernamePasswordAuthenticationTokenBuilder.build(userDetails, request)
                );
            }
        } catch (Exception exception) {
            log.error("Cannot set user authentication: {}", exception.getMessage());
        }
        filterChain.doFilter(request, response);
    }
}
