package com.acme.coldtrace.platform.iam.domain.repositories;

import com.acme.coldtrace.platform.iam.domain.model.aggregates.ExternalIdentity;
import com.acme.coldtrace.platform.iam.domain.model.valueobjects.SocialProvider;

import java.util.Optional;

/**
 * Domain repository contract for external identity links.
 *
 * @since 1.0
 */
public interface ExternalIdentityRepository {
    /**
     * Finds an identity by provider and provider subject.
     *
     * @param provider external provider
     * @param providerSubject stable subject issued by the provider
     * @return matching identity when linked
     */
    Optional<ExternalIdentity> findByProviderAndProviderSubject(SocialProvider provider, String providerSubject);

    /**
     * Persists an external identity link.
     *
     * @param externalIdentity identity to persist
     * @return persisted identity
     */
    ExternalIdentity save(ExternalIdentity externalIdentity);
}
