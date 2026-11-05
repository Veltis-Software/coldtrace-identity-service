package com.acme.coldtrace.platform.iam.interfaces.rest.resources;

/**
 * Request resource used to create an organization.
 *
 * @param legalName organization legal name
 * @param commercialName organization commercial name
 * @param taxId optional tax identifier
 * @param contactEmail organization contact email
 * @since 1.0
 */
public record CreateOrganizationResource(
        String legalName,
        String commercialName,
        String taxId,
        String contactEmail
) {
}
