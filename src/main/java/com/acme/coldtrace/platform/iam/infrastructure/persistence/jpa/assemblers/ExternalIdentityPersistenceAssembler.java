package com.acme.coldtrace.platform.iam.infrastructure.persistence.jpa.assemblers;

import com.acme.coldtrace.platform.iam.domain.model.aggregates.ExternalIdentity;
import com.acme.coldtrace.platform.iam.domain.model.valueobjects.SocialProvider;
import com.acme.coldtrace.platform.iam.infrastructure.persistence.jpa.entities.ExternalIdentityPersistenceEntity;

/**
 * Assembler that translates external identities between domain and persistence models.
 *
 * @since 1.0
 */
public final class ExternalIdentityPersistenceAssembler {
    private ExternalIdentityPersistenceAssembler() {
    }

    public static ExternalIdentity toDomainFromPersistence(ExternalIdentityPersistenceEntity entity) {
        return new ExternalIdentity(
                entity.getId(),
                SocialProvider.fromCode(entity.getProvider()),
                entity.getProviderSubject(),
                entity.getEmail(),
                entity.getUserId()
        );
    }

    public static ExternalIdentityPersistenceEntity toPersistenceFromDomain(ExternalIdentity externalIdentity) {
        var entity = new ExternalIdentityPersistenceEntity();
        entity.setId(externalIdentity.getId());
        copyDomainState(externalIdentity, entity);
        return entity;
    }

    public static void copyDomainState(ExternalIdentity externalIdentity, ExternalIdentityPersistenceEntity entity) {
        entity.setProvider(externalIdentity.getProvider().code());
        entity.setProviderSubject(externalIdentity.getProviderSubject());
        entity.setEmail(externalIdentity.getEmail());
        entity.setUserId(externalIdentity.getUserId());
    }
}
