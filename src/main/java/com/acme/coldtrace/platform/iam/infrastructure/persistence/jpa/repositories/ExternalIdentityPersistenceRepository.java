package com.acme.coldtrace.platform.iam.infrastructure.persistence.jpa.repositories;

import com.acme.coldtrace.platform.iam.infrastructure.persistence.jpa.entities.ExternalIdentityPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for external identity persistence entities.
 *
 * @since 1.0
 */
@Repository
public interface ExternalIdentityPersistenceRepository extends JpaRepository<ExternalIdentityPersistenceEntity, Long> {
    /**
     * Finds one external identity by provider and provider subject.
     *
     * @param provider provider code
     * @param providerSubject stable provider subject
     * @return persistence entity when linked
     */
    Optional<ExternalIdentityPersistenceEntity> findByProviderAndProviderSubject(
            String provider,
            String providerSubject
    );
}
