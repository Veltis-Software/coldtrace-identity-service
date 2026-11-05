package com.acme.coldtrace.platform.iam.interfaces.rest.resources;

/**
 * Response resource representing an organization.
 *
 * @param id organization identifier
 * @param legalName organization legal name
 * @param commercialName organization commercial name
 * @param taxId organization tax identifier
 * @param contactEmail organization contact email
 * @since 1.0
 */
public record OrganizationResource(
        Long id,
        String legalName,
        String commercialName,
        String taxId,
        String contactEmail
) {
}
