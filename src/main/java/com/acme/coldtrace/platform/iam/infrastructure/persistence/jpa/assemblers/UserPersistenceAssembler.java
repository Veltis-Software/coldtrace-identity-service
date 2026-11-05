package com.acme.coldtrace.platform.iam.infrastructure.persistence.jpa.assemblers;

import com.acme.coldtrace.platform.iam.domain.model.aggregates.User;
import com.acme.coldtrace.platform.iam.infrastructure.persistence.jpa.entities.UserPersistenceEntity;

/**
 * Assembler that translates users between domain and persistence models.
 *
 * @since 1.0
 */
public final class UserPersistenceAssembler {
    private UserPersistenceAssembler() {
    }

    public static User toDomainFromPersistence(UserPersistenceEntity entity) {
        return new User(
                entity.getId(),
                entity.getUuid(),
                entity.getOrganizationUserId(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getEmail(),
                entity.getPasswordHash(),
                entity.getOrganizationId(),
                entity.getRoleId()
        );
    }

    public static UserPersistenceEntity toPersistenceFromDomain(User user) {
        var entity = new UserPersistenceEntity();
        entity.setId(user.getId());
        copyDomainState(user, entity);
        return entity;
    }

    public static void copyDomainState(User user, UserPersistenceEntity entity) {
        entity.setUuid(user.getUuid());
        entity.setOrganizationUserId(user.getOrganizationUserId());
        entity.setFirstName(user.getFirstName());
        entity.setLastName(user.getLastName());
        entity.setEmail(user.getEmailValue());
        entity.setPasswordHash(user.getPasswordHash());
        entity.setOrganizationId(user.getOrganizationId());
        entity.setRoleId(user.getRoleId());
    }
}
