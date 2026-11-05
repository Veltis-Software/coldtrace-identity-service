package com.acme.coldtrace.platform.iam.application.commandservices;

/**
 * Failure types for organization command execution.
 *
 * @since 1.0
 */
public sealed interface OrganizationCommandFailure
        permits OrganizationCommandFailure.DuplicateContactEmail,
        OrganizationCommandFailure.DuplicateTaxId {
    /**
     * Returns the message key associated with the failure.
     *
     * @return message key to resolve through i18n
     */
    String messageKey();

    /**
     * Returns optional arguments for message interpolation.
     *
     * @return message interpolation arguments
     */
    default Object[] args() {
        return new Object[0];
    }

    /**
     * Duplicate organization contact email failure.
     */
    record DuplicateContactEmail() implements OrganizationCommandFailure {
        @Override
        public String messageKey() {
            return "identity-access.organization.error.contactEmail.duplicate";
        }
    }

    /**
     * Duplicate organization tax identifier failure.
     */
    record DuplicateTaxId() implements OrganizationCommandFailure {
        @Override
        public String messageKey() {
            return "identity-access.organization.error.taxId.duplicate";
        }
    }
}
