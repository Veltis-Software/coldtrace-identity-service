package com.acme.coldtrace.platform.iam.infrastructure.persistence.jpa.repositories;

import com.acme.coldtrace.platform.iam.domain.model.valueobjects.EmailAddress;
import com.acme.coldtrace.platform.iam.infrastructure.persistence.jpa.entities.UserPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for user persistence entities.
 *
 * @since 1.0
 */
@Repository
public interface UserPersistenceRepository extends JpaRepository<UserPersistenceEntity, Long> {
    /**
     * Finds users by organization.
     *
     * @param organizationId organization identifier
     * @return persistence entities for the organization
     */
    List<UserPersistenceEntity> findAllByOrganizationId(Long organizationId);

    /**
     * Finds one user by identifier and organization.
     *
     * @param id user identifier
     * @param organizationId organization identifier
     * @return persistence entity when the user belongs to the organization
     */
    Optional<UserPersistenceEntity> findByIdAndOrganizationId(Long id, Long organizationId);

    /**
     * Finds one user by email.
     *
     * @param email user email value object
     * @return persistence entity when the email exists
     */
    Optional<UserPersistenceEntity> findByEmail(EmailAddress email);

    /**
     * Checks whether an email exists.
     *
     * @param email user email value object
     * @return true when the email exists
     */
    boolean existsByEmail(EmailAddress email);
}
