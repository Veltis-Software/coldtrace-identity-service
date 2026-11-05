package com.acme.coldtrace.platform.iam.interfaces.rest.transform;

import com.acme.coldtrace.platform.iam.domain.model.aggregates.Organization;
import com.acme.coldtrace.platform.iam.interfaces.rest.resources.OrganizationResource;

/**
 * Interface layer translator converting organization aggregates to resources.
 *
 * @since 1.0
 */
public class OrganizationResourceFromEntityAssembler {
    /**
     * Converts an organization aggregate to an OrganizationResource.
     *
     * @param entity organization aggregate
     * @return organization response resource
     */
    public static OrganizationResource toResourceFromEntity(Organization entity) {
        return new OrganizationResource(
                entity.getId(),
                entity.getLegalName(),
                entity.getCommercialName(),
                entity.getTaxId(),
                entity.getContactEmail()
        );
    }
}
