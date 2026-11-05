package com.acme.coldtrace.platform.iam.infrastructure.persistence.jpa.assemblers;

import com.acme.coldtrace.platform.iam.domain.model.aggregates.PasswordResetRequest;
import com.acme.coldtrace.platform.iam.infrastructure.persistence.jpa.entities.PasswordResetRequestPersistenceEntity;

/**
 * Assembler that translates password reset requests between domain and persistence models.
 *
 * @since 1.0
 */
public final class PasswordResetRequestPersistenceAssembler {
    private PasswordResetRequestPersistenceAssembler() {
    }

    public static PasswordResetRequest toDomainFromPersistence(PasswordResetRequestPersistenceEntity entity) {
        return new PasswordResetRequest(
                entity.getId(),
                entity.getEmail(),
                entity.getUserId(),
                entity.getTokenHash(),
                entity.getRequestedAt(),
                entity.getExpiresAt(),
                entity.getConsumedAt()
        );
    }

    public static PasswordResetRequestPersistenceEntity toPersistenceFromDomain(
            PasswordResetRequest passwordResetRequest
    ) {
        var entity = new PasswordResetRequestPersistenceEntity();
        entity.setId(passwordResetRequest.getId());
        copyDomainState(passwordResetRequest, entity);
        return entity;
    }

    public static void copyDomainState(
            PasswordResetRequest passwordResetRequest,
            PasswordResetRequestPersistenceEntity entity
    ) {
        entity.setEmail(passwordResetRequest.getEmailValue());
        entity.setUserId(passwordResetRequest.getUserId());
        entity.setTokenHash(passwordResetRequest.getTokenHash());
        entity.setRequestedAt(passwordResetRequest.getRequestedAt());
        entity.setExpiresAt(passwordResetRequest.getExpiresAt());
        entity.setConsumedAt(passwordResetRequest.getConsumedAt());
    }
}
