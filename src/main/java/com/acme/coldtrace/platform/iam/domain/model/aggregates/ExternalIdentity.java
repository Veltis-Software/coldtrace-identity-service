package com.acme.coldtrace.platform.iam.domain.model.aggregates;

import com.acme.coldtrace.platform.iam.domain.model.valueobjects.SocialProvider;
import com.acme.coldtrace.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;

/**
 * External provider identity linked to a local ColdTrace user.
 *
 * @since 1.0
 */
@Getter
public class ExternalIdentity extends AbstractDomainAggregateRoot<ExternalIdentity> {
    private Long id;
    private SocialProvider provider;
    private String providerSubject;
    private String email;
    private Long userId;

    protected ExternalIdentity() {
    }

    public ExternalIdentity(SocialProvider provider, String providerSubject, String email, Long userId) {
        this(null, provider, providerSubject, email, userId);
    }

    public ExternalIdentity(Long id, SocialProvider provider, String providerSubject, String email, Long userId) {
        if (provider == null) {
            throw new IllegalArgumentException("identity-access.authentication.error.provider.required");
        }
        if (providerSubject == null || providerSubject.isBlank()) {
            throw new IllegalArgumentException("identity-access.authentication.error.provider-subject.required");
        }
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("identity-access.user.error.userId.invalid");
        }
        this.id = id;
        this.provider = provider;
        this.providerSubject = providerSubject.trim();
        this.email = email != null && !email.isBlank() ? email.trim().toLowerCase() : null;
        this.userId = userId;
    }
}
