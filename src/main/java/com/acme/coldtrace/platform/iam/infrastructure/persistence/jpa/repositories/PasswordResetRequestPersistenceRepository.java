package com.acme.coldtrace.platform.iam.infrastructure.persistence.jpa.repositories;

import com.acme.coldtrace.platform.iam.infrastructure.persistence.jpa.entities.PasswordResetRequestPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for password reset request persistence entities.
 *
 * @since 1.0
 */
@Repository
public interface PasswordResetRequestPersistenceRepository
        extends JpaRepository<PasswordResetRequestPersistenceEntity, Long> {
    /**
     * Finds one password reset request by token hash.
     *
     * @param tokenHash token hash
     * @return persistence entity when found
     */
    Optional<PasswordResetRequestPersistenceEntity> findByTokenHash(String tokenHash);
}
