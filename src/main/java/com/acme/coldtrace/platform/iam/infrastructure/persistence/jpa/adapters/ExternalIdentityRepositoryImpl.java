package com.acme.coldtrace.platform.iam.infrastructure.persistence.jpa.adapters;

import com.acme.coldtrace.platform.iam.domain.model.aggregates.ExternalIdentity;
import com.acme.coldtrace.platform.iam.domain.model.valueobjects.SocialProvider;
import com.acme.coldtrace.platform.iam.domain.repositories.ExternalIdentityRepository;
import com.acme.coldtrace.platform.iam.infrastructure.persistence.jpa.assemblers.ExternalIdentityPersistenceAssembler;
import com.acme.coldtrace.platform.iam.infrastructure.persistence.jpa.repositories.ExternalIdentityPersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * JPA-backed adapter for external identity links.
 *
 * @since 1.0
 */
@Repository
public class ExternalIdentityRepositoryImpl implements ExternalIdentityRepository {
    private final ExternalIdentityPersistenceRepository externalIdentityPersistenceRepository;

    public ExternalIdentityRepositoryImpl(
            ExternalIdentityPersistenceRepository externalIdentityPersistenceRepository
    ) {
        this.externalIdentityPersistenceRepository = externalIdentityPersistenceRepository;
    }

    @Override
    public Optional<ExternalIdentity> findByProviderAndProviderSubject(
            SocialProvider provider,
            String providerSubject
    ) {
        return externalIdentityPersistenceRepository
                .findByProviderAndProviderSubject(provider.code(), providerSubject)
                .map(ExternalIdentityPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public ExternalIdentity save(ExternalIdentity externalIdentity) {
        if (externalIdentity.getId() == null) {
            var entity = ExternalIdentityPersistenceAssembler.toPersistenceFromDomain(externalIdentity);
            return ExternalIdentityPersistenceAssembler.toDomainFromPersistence(
                    externalIdentityPersistenceRepository.save(entity)
            );
        }

        var entity = externalIdentityPersistenceRepository.findById(externalIdentity.getId())
                .orElseGet(() -> ExternalIdentityPersistenceAssembler.toPersistenceFromDomain(externalIdentity));
        ExternalIdentityPersistenceAssembler.copyDomainState(externalIdentity, entity);
        return ExternalIdentityPersistenceAssembler.toDomainFromPersistence(
                externalIdentityPersistenceRepository.save(entity)
        );
    }
}
