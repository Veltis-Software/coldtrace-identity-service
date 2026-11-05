package com.acme.coldtrace.platform.iam.infrastructure.tokens.jwt.services;

import com.acme.coldtrace.platform.iam.infrastructure.tokens.jwt.BearerTokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.function.Function;

/**
 * JWT bearer token service.
 *
 * @since 1.0
 */
@Slf4j
@Service
public class TokenServiceImpl implements BearerTokenService {
    private static final String AUTHORIZATION_HEADER_NAME = "Authorization";
    private static final String BEARER_TOKEN_PREFIX = "Bearer ";
    private static final int TOKEN_BEGIN_INDEX = 7;

    @Value("${authorization.jwt.secret}")
    private String secret;

    @Value("${authorization.jwt.expiration.days}")
    private int expirationDays;

    @Override
    public String generateToken(Authentication authentication) {
        return buildTokenWithDefaultParameters(authentication.getName());
    }

    @Override
    public String generateToken(String email) {
        return buildTokenWithDefaultParameters(email);
    }

    @Override
    public String getEmailFromToken(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token);
            return true;
        } catch (SignatureException exception) {
            log.error("Invalid JSON Web Token signature: {}", exception.getMessage());
        } catch (MalformedJwtException exception) {
            log.error("Invalid JSON Web Token: {}", exception.getMessage());
        } catch (ExpiredJwtException exception) {
            log.error("JSON Web Token is expired: {}", exception.getMessage());
        } catch (UnsupportedJwtException exception) {
            log.error("JSON Web Token is unsupported: {}", exception.getMessage());
        } catch (IllegalArgumentException exception) {
            log.error("JSON Web Token claims string is empty: {}", exception.getMessage());
        }
        return false;
    }

    @Override
    public String getBearerTokenFrom(HttpServletRequest request) {
        var authorizationHeader = request.getHeader(AUTHORIZATION_HEADER_NAME);
        if (StringUtils.hasText(authorizationHeader) && authorizationHeader.startsWith(BEARER_TOKEN_PREFIX)) {
            return authorizationHeader.substring(TOKEN_BEGIN_INDEX);
        }
        return null;
    }

    private String buildTokenWithDefaultParameters(String email) {
        var issuedAt = Instant.now();
        var expiration = issuedAt.plus(expirationDays, ChronoUnit.DAYS);
        return Jwts.builder()
                .subject(email)
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(expiration))
                .signWith(getSigningKey())
                .compact();
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        return claimsResolver.apply(extractAllClaims(token));
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
    }

    private SecretKey getSigningKey() {
        var keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
