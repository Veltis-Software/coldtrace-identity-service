package com.acme.coldtrace.platform.iam.domain.model.commands;

/**
 * Command for creating an organization.
 *
 * @param legalName organization legal name
 * @param commercialName organization commercial name
 * @param taxId optional tax identifier
 * @param contactEmail organization contact email
 * @since 1.0
 */
public record CreateOrganizationCommand(
        String legalName,
        String commercialName,
        String taxId,
        String contactEmail
) {
    /**
     * Validates and normalizes organization creation data.
     *
     * @throws IllegalArgumentException if required fields are blank or contact email is invalid
     */
    public CreateOrganizationCommand {
        legalName = requireNonBlank(legalName, "identity-access.organization.error.legalName.required");
        commercialName = requireNonBlank(commercialName, "identity-access.organization.error.commercialName.required");
        taxId = normalizeOptional(taxId);
        contactEmail = requireNonBlank(contactEmail, "identity-access.organization.error.contactEmail.required")
                .toLowerCase();
        if (!contactEmail.contains("@")) {
            throw new IllegalArgumentException("identity-access.organization.error.contactEmail.invalid");
        }
    }

    /**
     * Requires a non-blank string value and returns it trimmed.
     *
     * @param value input value
     * @param messageKey message key used when the value is blank
     * @return trimmed input value
     */
    private static String requireNonBlank(String value, String messageKey) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(messageKey);
        }
        return value.trim();
    }

    /**
     * Normalizes optional string input.
     *
     * @param value input value
     * @return trimmed value or null when blank
     */
    private static String normalizeOptional(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
