package com.acme.coldtrace.platform.iam.infrastructure.oauth.services;

import com.acme.coldtrace.platform.iam.application.internal.outboundservices.social.ExternalIdentityProviderService;
import com.acme.coldtrace.platform.iam.application.internal.outboundservices.social.ProviderIdentity;
import com.acme.coldtrace.platform.iam.domain.model.commands.SocialSignInCommand;
import com.acme.coldtrace.platform.iam.domain.model.valueobjects.SocialProvider;
import com.acme.coldtrace.platform.shared.application.result.ApplicationError;
import com.acme.coldtrace.platform.shared.application.result.Result;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * OIDC provider integration for Google and Apple social authentication.
 *
 * @since 1.0
 */
@Slf4j
@Service
public class OidcExternalIdentityProviderService implements ExternalIdentityProviderService {
    private static final Duration HTTP_TIMEOUT = Duration.ofSeconds(10);
    private static final Duration JWKS_CACHE_TTL = Duration.ofMinutes(10);
    private static final String APPLE_AUDIENCE = "https://appleid.apple.com";

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient;
    private final Map<SocialProvider, ProviderConfiguration> providerConfigurations;
    private final Map<SocialProvider, CachedJwks> jwksCache = new ConcurrentHashMap<>();

    public OidcExternalIdentityProviderService(
            @Value("${authentication.social.google.client-id:}") String googleClientId,
            @Value("${authentication.social.google.client-secret:}") String googleClientSecret,
            @Value("${authentication.social.google.redirect-uri:}") String googleRedirectUri,
            @Value("${authentication.social.google.token-uri:https://oauth2.googleapis.com/token}") String googleTokenUri,
            @Value("${authentication.social.google.jwks-uri:https://www.googleapis.com/oauth2/v3/certs}") String googleJwksUri,
            @Value("${authentication.social.google.issuer:https://accounts.google.com}") String googleIssuer,
            @Value("${authentication.social.apple.client-id:}") String appleClientId,
            @Value("${authentication.social.apple.redirect-uri:}") String appleRedirectUri,
            @Value("${authentication.social.apple.token-uri:https://appleid.apple.com/auth/token}") String appleTokenUri,
            @Value("${authentication.social.apple.jwks-uri:https://appleid.apple.com/auth/keys}") String appleJwksUri,
            @Value("${authentication.social.apple.issuer:https://appleid.apple.com}") String appleIssuer,
            @Value("${authentication.social.apple.team-id:}") String appleTeamId,
            @Value("${authentication.social.apple.key-id:}") String appleKeyId,
            @Value("${authentication.social.apple.private-key:}") String applePrivateKey
    ) {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
        this.providerConfigurations = Map.of(
                SocialProvider.GOOGLE,
                new ProviderConfiguration(
                        googleClientId,
                        googleClientSecret,
                        googleRedirectUri,
                        googleTokenUri,
                        googleJwksUri,
                        List.of(googleIssuer, "accounts.google.com"),
                        null,
                        null,
                        null
                ),
                SocialProvider.APPLE,
                new ProviderConfiguration(
                        appleClientId,
                        null,
                        appleRedirectUri,
                        appleTokenUri,
                        appleJwksUri,
                        List.of(appleIssuer),
                        appleTeamId,
                        appleKeyId,
                        applePrivateKey
                )
        );
    }

    @Override
    public Result<ProviderIdentity, ApplicationError> validate(SocialSignInCommand command) {
        var configuration = providerConfigurations.get(command.provider());
        if (configuration == null || !hasText(configuration.clientId())) {
            log.warn("Social provider client id is not configured: provider={}", command.provider().code());
            return providerConfigurationMissing();
        }

        var idTokenResult = resolveIdToken(command, configuration);
        if (idTokenResult.isFailure()) {
            return Result.failure(idTokenResult.failure().orElseThrow());
        }

        try {
            var idToken = idTokenResult.success().orElseThrow();
            var claims = validateIdToken(command, configuration, idToken);
            var subject = claims.getSubject();
            if (!hasText(subject)) {
                return providerValidationFailed();
            }
            var email = normalizeNullableEmail(claims.get("email", String.class));
            var fullName = normalizeNullableText(claims.get("name", String.class));
            var givenName = normalizeNullableText(claims.get("given_name", String.class));
            var familyName = normalizeNullableText(claims.get("family_name", String.class));
            return Result.success(new ProviderIdentity(
                    command.provider(),
                    subject,
                    email,
                    fullName,
                    givenName,
                    familyName,
                    idToken
            ));
        } catch (Exception exception) {
            log.warn("Provider token validation failed: provider={}, reason={}",
                    command.provider().code(), exception.getMessage());
            return providerValidationFailed();
        }
    }

    private Result<String, ApplicationError> resolveIdToken(
            SocialSignInCommand command,
            ProviderConfiguration configuration
    ) {
        if (hasText(command.idToken())) {
            return Result.success(command.idToken());
        }
        return exchangeAuthorizationCode(command, configuration);
    }

    private Result<String, ApplicationError> exchangeAuthorizationCode(
            SocialSignInCommand command,
            ProviderConfiguration configuration
    ) {
        try {
            var redirectUri = hasText(command.redirectUri()) ? command.redirectUri() : configuration.redirectUri();
            var formValues = new LinkedHashMap<String, String>();
            formValues.put("grant_type", "authorization_code");
            formValues.put("code", command.authorizationCode());
            formValues.put("client_id", configuration.clientId());
            if (hasText(redirectUri)) {
                formValues.put("redirect_uri", redirectUri);
            }
            var clientSecretResult = clientSecretFor(command.provider(), configuration);
            if (clientSecretResult.isFailure()) {
                return Result.failure(clientSecretResult.failure().orElseThrow());
            }
            formValues.put("client_secret", clientSecretResult.success().orElseThrow());

            var request = HttpRequest.newBuilder(URI.create(configuration.tokenUri()))
                    .timeout(HTTP_TIMEOUT)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(toFormBody(formValues)))
                    .build();
            var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 400) {
                log.warn("Provider authorization code exchange failed: provider={}, status={}",
                        command.provider().code(), response.statusCode());
                return providerValidationFailed();
            }

            var responseBody = objectMapper.readValue(
                    response.body(),
                    new TypeReference<Map<String, Object>>() {
                    }
            );
            var idToken = responseBody.get("id_token");
            if (!(idToken instanceof String token) || token.isBlank()) {
                log.warn("Provider token response did not include id_token: provider={}", command.provider().code());
                return providerValidationFailed();
            }
            return Result.success(token);
        } catch (Exception exception) {
            log.warn("Provider authorization code exchange failed: provider={}, reason={}",
                    command.provider().code(), exception.getMessage());
            return providerValidationFailed();
        }
    }

    private Claims validateIdToken(
            SocialSignInCommand command,
            ProviderConfiguration configuration,
            String idToken
    ) throws IOException, InterruptedException, GeneralSecurityException {
        var header = decodeJwtSegment(idToken, 0);
        var keyId = header.get("kid");
        if (!(keyId instanceof String kid) || kid.isBlank()) {
            throw new IllegalArgumentException("Missing key id");
        }
        var algorithm = header.get("alg");
        if (!(algorithm instanceof String alg) || !"RS256".equals(alg)) {
            throw new IllegalArgumentException("Unsupported token algorithm");
        }

        var publicKey = publicKeyFor(command.provider(), configuration, kid);
        var claims = Jwts.parser()
                .verifyWith(publicKey)
                .build()
                .parseSignedClaims(idToken)
                .getPayload();

        if (!configuration.issuers().contains(claims.getIssuer())) {
            throw new IllegalArgumentException("Invalid token issuer");
        }
        if (!hasExpectedAudience(claims.get("aud"), configuration.clientId())) {
            throw new IllegalArgumentException("Invalid token audience");
        }
        if (hasText(command.nonce()) && !command.nonce().equals(claims.get("nonce", String.class))) {
            throw new IllegalArgumentException("Invalid token nonce");
        }
        if (!isEmailVerified(claims.get("email_verified"))) {
            throw new IllegalArgumentException("Provider email is not verified");
        }
        return claims;
    }

    private PublicKey publicKeyFor(
            SocialProvider provider,
            ProviderConfiguration configuration,
            String keyId
    ) throws IOException, InterruptedException, GeneralSecurityException {
        var jwk = jwkFor(provider, configuration, keyId);
        if (!"RSA".equals(jwk.get("kty"))) {
            throw new IllegalArgumentException("Unsupported JWK key type");
        }
        var modulus = new BigInteger(1, Base64.getUrlDecoder().decode(Objects.toString(jwk.get("n"), "")));
        var exponent = new BigInteger(1, Base64.getUrlDecoder().decode(Objects.toString(jwk.get("e"), "")));
        return KeyFactory.getInstance("RSA").generatePublic(new RSAPublicKeySpec(modulus, exponent));
    }

    private Map<String, Object> jwkFor(
            SocialProvider provider,
            ProviderConfiguration configuration,
            String keyId
    ) throws IOException, InterruptedException {
        var keys = loadJwks(provider, configuration);
        var jwk = keys.keysById().get(keyId);
        if (jwk == null) {
            jwksCache.remove(provider);
            jwk = loadJwks(provider, configuration).keysById().get(keyId);
        }
        if (jwk == null) {
            throw new IllegalArgumentException("Provider public key was not found");
        }
        return jwk;
    }

    private CachedJwks loadJwks(
            SocialProvider provider,
            ProviderConfiguration configuration
    ) throws IOException, InterruptedException {
        var cachedJwks = jwksCache.get(provider);
        if (cachedJwks != null && cachedJwks.expiresAt().isAfter(Instant.now())) {
            return cachedJwks;
        }

        var request = HttpRequest.newBuilder(URI.create(configuration.jwksUri()))
                .timeout(HTTP_TIMEOUT)
                .GET()
                .build();
        var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() >= 400) {
            throw new IllegalArgumentException("Provider JWKS endpoint failed");
        }
        var responseBody = objectMapper.readValue(
                response.body(),
                new TypeReference<Map<String, Object>>() {
                }
        );
        var keys = responseBody.get("keys");
        if (!(keys instanceof List<?> keyList)) {
            throw new IllegalArgumentException("Provider JWKS response is invalid");
        }
        var keysById = new LinkedHashMap<String, Map<String, Object>>();
        keyList.stream()
                .filter(Map.class::isInstance)
                .map(key -> (Map<?, ?>) key)
                .filter(key -> key.get("kid") instanceof String)
                .forEach(key -> {
                    var normalizedKey = new LinkedHashMap<String, Object>();
                    key.forEach((entryKey, entryValue) -> normalizedKey.put(entryKey.toString(), entryValue));
                    keysById.put((String) key.get("kid"), normalizedKey);
                });
        var newCache = new CachedJwks(keysById, Instant.now().plus(JWKS_CACHE_TTL));
        jwksCache.put(provider, newCache);
        return newCache;
    }

    private Result<String, ApplicationError> clientSecretFor(
            SocialProvider provider,
            ProviderConfiguration configuration
    ) {
        if (provider == SocialProvider.GOOGLE) {
            if (!hasText(configuration.clientSecret())) {
                log.warn("Google client secret is not configured");
                return providerConfigurationMissing();
            }
            return Result.success(configuration.clientSecret());
        }

        if (!hasText(configuration.appleTeamId()) ||
                !hasText(configuration.appleKeyId()) ||
                !hasText(configuration.applePrivateKey())) {
            log.warn("Apple client secret configuration is incomplete");
            return providerConfigurationMissing();
        }
        try {
            var now = Instant.now();
            return Result.success(Jwts.builder()
                    .issuer(configuration.appleTeamId())
                    .subject(configuration.clientId())
                    .audience().add(APPLE_AUDIENCE).and()
                    .issuedAt(Date.from(now))
                    .expiration(Date.from(now.plus(5, ChronoUnit.MINUTES)))
                    .header().keyId(configuration.appleKeyId()).and()
                    .signWith(applePrivateKeyFrom(configuration.applePrivateKey()), Jwts.SIG.ES256)
                    .compact());
        } catch (GeneralSecurityException exception) {
            log.warn("Apple private key configuration is invalid: reason={}", exception.getMessage());
            return providerConfigurationMissing();
        }
    }

    private ECPrivateKey applePrivateKeyFrom(String rawPrivateKey) throws GeneralSecurityException {
        var normalizedKey = rawPrivateKey
                .replace("\\n", "\n")
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");
        var keyBytes = Base64.getDecoder().decode(normalizedKey);
        return (ECPrivateKey) KeyFactory.getInstance("EC")
                .generatePrivate(new PKCS8EncodedKeySpec(keyBytes));
    }

    private Map<String, Object> decodeJwtSegment(String jwt, int segmentIndex) throws IOException {
        var segments = jwt.split("\\.");
        if (segments.length != 3 || segmentIndex >= segments.length) {
            throw new IllegalArgumentException("Invalid JWT format");
        }
        var decoded = Base64.getUrlDecoder().decode(segments[segmentIndex]);
        return objectMapper.readValue(decoded, new TypeReference<Map<String, Object>>() {
        });
    }

    private boolean hasExpectedAudience(Object audienceClaim, String clientId) {
        if (audienceClaim instanceof String audience) {
            return clientId.equals(audience);
        }
        if (audienceClaim instanceof Collection<?> audiences) {
            return audiences.stream().anyMatch(clientId::equals);
        }
        return false;
    }

    private boolean isEmailVerified(Object emailVerifiedClaim) {
        if (emailVerifiedClaim == null) {
            return true;
        }
        if (emailVerifiedClaim instanceof Boolean verified) {
            return verified;
        }
        if (emailVerifiedClaim instanceof String verified) {
            return Boolean.parseBoolean(verified);
        }
        return false;
    }

    private <T> Result<T, ApplicationError> providerValidationFailed() {
        return Result.failure(ApplicationError.providerValidationFailed(
                "identity-access.authentication.error.provider-validation-failed"
        ));
    }

    private <T> Result<T, ApplicationError> providerConfigurationMissing() {
        return Result.failure(ApplicationError.socialProviderConfigurationMissing(
                "identity-access.authentication.error.provider-configuration-missing"
        ));
    }

    private String toFormBody(Map<String, String> values) {
        return values.entrySet().stream()
                .filter(entry -> hasText(entry.getValue()))
                .map(entry -> "%s=%s".formatted(
                        urlEncode(entry.getKey()),
                        urlEncode(entry.getValue())
                ))
                .collect(Collectors.joining("&"));
    }

    private String urlEncode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private String normalizeNullableEmail(String value) {
        return hasText(value) ? value.trim().toLowerCase() : null;
    }

    private String normalizeNullableText(String value) {
        return hasText(value) ? value.trim() : null;
    }

    private record ProviderConfiguration(
            String clientId,
            String clientSecret,
            String redirectUri,
            String tokenUri,
            String jwksUri,
            List<String> issuers,
            String appleTeamId,
            String appleKeyId,
            String applePrivateKey
    ) {
    }

    private record CachedJwks(Map<String, Map<String, Object>> keysById, Instant expiresAt) {
    }
}
