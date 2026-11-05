package com.acme.coldtrace.platform.iam.domain.repositories;

import com.acme.coldtrace.platform.iam.domain.model.aggregates.Organization;

import java.util.List;

/**
 * Domain repository contract for organization aggregates.
 *
 * @since 1.0
 */
public interface OrganizationRepository {
    /**
     * Finds all organizations.
     *
     * @return persisted organizations
     */
    List<Organization> findAll();

    /**
     * Checks whether an organization exists by identifier.
     *
     * @param id organization identifier
     * @return true when the organization exists
     */
    boolean existsById(Long id);

    /**
     * Persists an organization aggregate.
     *
     * @param organization organization aggregate
     * @return persisted organization rebuilt from persistence state
     */
    Organization save(Organization organization);

    /**
     * Checks whether a contact email is already used by an organization.
     *
     * @param contactEmail contact email
     * @return true when the contact email exists
     */
    boolean existsByContactEmail(String contactEmail);

    /**
     * Checks whether a tax identifier is already used by an organization.
     *
     * @param taxId tax identifier
     * @return true when the tax identifier exists
     */
    boolean existsByTaxId(String taxId);
}
