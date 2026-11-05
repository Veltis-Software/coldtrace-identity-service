package com.acme.coldtrace.platform.iam.infrastructure.persistence.jpa.repositories;

import com.acme.coldtrace.platform.iam.domain.model.valueobjects.EmailAddress;
import com.acme.coldtrace.platform.iam.infrastructure.persistence.jpa.entities.OrganizationPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for organization persistence entities.
 *
 * @since 1.0
 */
@Repository
public interface OrganizationPersistenceRepository extends JpaRepository<OrganizationPersistenceEntity, Long> {
    /**
     * Checks whether a contact email exists.
     *
     * @param contactEmail contact email value object
     * @return true when the contact email exists
     */
    boolean existsByContactEmail(EmailAddress contactEmail);

    /**
     * Checks whether a tax identifier exists ignoring case.
     *
     * @param taxId organization tax identifier
     * @return true when the tax identifier exists
     */
    boolean existsByTaxIdIgnoreCase(String taxId);
}
