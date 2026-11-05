package com.acme.coldtrace.platform.iam.infrastructure.persistence.jpa.adapters;

import com.acme.coldtrace.platform.iam.domain.model.aggregates.Organization;
import com.acme.coldtrace.platform.iam.domain.model.valueobjects.EmailAddress;
import com.acme.coldtrace.platform.iam.domain.repositories.OrganizationRepository;
import com.acme.coldtrace.platform.iam.infrastructure.persistence.jpa.assemblers.OrganizationPersistenceAssembler;
import com.acme.coldtrace.platform.iam.infrastructure.persistence.jpa.repositories.OrganizationPersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * JPA-backed adapter for the organization domain repository.
 *
 * @since 1.0
 */
@Repository
public class OrganizationRepositoryImpl implements OrganizationRepository {
    private final OrganizationPersistenceRepository organizationPersistenceRepository;

    public OrganizationRepositoryImpl(OrganizationPersistenceRepository organizationPersistenceRepository) {
        this.organizationPersistenceRepository = organizationPersistenceRepository;
    }

    @Override
    public List<Organization> findAll() {
        return organizationPersistenceRepository.findAll().stream()
                .map(OrganizationPersistenceAssembler::toDomainFromPersistence)
                .toList();
    }

    @Override
    public boolean existsById(Long id) {
        return organizationPersistenceRepository.existsById(id);
    }

    @Override
    public Organization save(Organization organization) {
        if (organization.getId() == null) {
            var entity = OrganizationPersistenceAssembler.toPersistenceFromDomain(organization);
            return OrganizationPersistenceAssembler.toDomainFromPersistence(
                    organizationPersistenceRepository.save(entity)
            );
        }

        var entity = organizationPersistenceRepository.findById(organization.getId())
                .orElseGet(() -> OrganizationPersistenceAssembler.toPersistenceFromDomain(organization));
        OrganizationPersistenceAssembler.copyDomainState(organization, entity);
        return OrganizationPersistenceAssembler.toDomainFromPersistence(
                organizationPersistenceRepository.save(entity)
        );
    }

    @Override
    public boolean existsByContactEmail(String contactEmail) {
        return organizationPersistenceRepository.existsByContactEmail(new EmailAddress(contactEmail));
    }

    @Override
    public boolean existsByTaxId(String taxId) {
        return organizationPersistenceRepository.existsByTaxIdIgnoreCase(taxId);
    }
}
