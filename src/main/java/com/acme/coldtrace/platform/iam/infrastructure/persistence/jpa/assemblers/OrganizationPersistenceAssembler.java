package com.acme.coldtrace.platform.iam.infrastructure.persistence.jpa.assemblers;

import com.acme.coldtrace.platform.iam.domain.model.aggregates.Organization;
import com.acme.coldtrace.platform.iam.infrastructure.persistence.jpa.entities.OrganizationPersistenceEntity;

/**
 * Assembler that translates organizations between domain and persistence models.
 *
 * @since 1.0
 */
public final class OrganizationPersistenceAssembler {
    private OrganizationPersistenceAssembler() {
    }

    public static Organization toDomainFromPersistence(OrganizationPersistenceEntity entity) {
        return new Organization(
                entity.getId(),
                entity.getLegalName(),
                entity.getCommercialName(),
                entity.getTaxId(),
                entity.getContactEmail()
        );
    }

    public static OrganizationPersistenceEntity toPersistenceFromDomain(Organization organization) {
        var entity = new OrganizationPersistenceEntity();
        entity.setId(organization.getId());
        copyDomainState(organization, entity);
        return entity;
    }

    public static void copyDomainState(Organization organization, OrganizationPersistenceEntity entity) {
        entity.setLegalName(organization.getLegalName());
        entity.setCommercialName(organization.getCommercialName());
        entity.setTaxId(organization.getTaxId());
        entity.setContactEmail(organization.getContactEmailValue());
    }
}
