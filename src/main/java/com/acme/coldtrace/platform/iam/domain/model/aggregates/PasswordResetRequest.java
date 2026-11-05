package com.acme.coldtrace.platform.iam.domain.model.aggregates;

import com.acme.coldtrace.platform.iam.domain.model.valueobjects.EmailAddress;
import com.acme.coldtrace.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;

import java.time.Instant;

/**
 * Password reset request aggregate for the IAM context.
 *
 * @since 1.0
 */
@Getter
public class PasswordResetRequest extends AbstractDomainAggregateRoot<PasswordResetRequest> {
    private Long id;
    private EmailAddress email;
    private Long userId;
    private String tokenHash;
    private Instant requestedAt;
    private Instant expiresAt;
    private Instant consumedAt;

    protected PasswordResetRequest() {
    }

    public PasswordResetRequest(String email, Long userId, String tokenHash, Instant requestedAt, Instant expiresAt) {
        this(null, new EmailAddress(email), userId, tokenHash, requestedAt, expiresAt, null);
    }

    public PasswordResetRequest(
            Long id,
            EmailAddress email,
            Long userId,
            String tokenHash,
            Instant requestedAt,
            Instant expiresAt,
            Instant consumedAt
    ) {
        if (email == null) {
            throw new IllegalArgumentException("identity-access.password-reset.error.email.required");
        }
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("identity-access.user.error.userId.invalid");
        }
        if (tokenHash == null || tokenHash.isBlank()) {
            throw new IllegalArgumentException("identity-access.password-reset.error.token.required");
        }
        if (requestedAt == null || expiresAt == null || !expiresAt.isAfter(requestedAt)) {
            throw new IllegalArgumentException("identity-access.password-reset.error.expiration.invalid");
        }
        this.id = id;
        this.email = email;
        this.userId = userId;
        this.tokenHash = tokenHash.trim();
        this.requestedAt = requestedAt;
        this.expiresAt = expiresAt;
        this.consumedAt = consumedAt;
    }

    /**
     * Returns the requested email as a string.
     *
     * @return email string
     */
    public String getEmail() {
        return this.email.value();
    }

    /**
     * Returns the strongly typed email value object.
     *
     * @return email value object
     */
    public EmailAddress getEmailValue() {
        return this.email;
    }
}
