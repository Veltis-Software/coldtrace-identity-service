package com.acme.coldtrace.platform.iam.application.commandservices;

/**
 * Failure types for organization sign-up command execution.
 *
 * @since 1.0
 */
public sealed interface OrganizationSignUpCommandFailure
        permits OrganizationSignUpCommandFailure.DuplicateOrganizationContactEmail,
        OrganizationSignUpCommandFailure.DuplicateOrganizationTaxId,
        OrganizationSignUpCommandFailure.DuplicateUserEmail,
        OrganizationSignUpCommandFailure.InitialRoleNotFound {
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
    record DuplicateOrganizationContactEmail() implements OrganizationSignUpCommandFailure {
        @Override
        public String messageKey() {
            return "identity-access.organization.error.contactEmail.duplicate";
        }
    }

    /**
     * Duplicate organization tax identifier failure.
     */
    record DuplicateOrganizationTaxId() implements OrganizationSignUpCommandFailure {
        @Override
        public String messageKey() {
            return "identity-access.organization.error.taxId.duplicate";
        }
    }

    /**
     * Duplicate first user email failure.
     */
    record DuplicateUserEmail() implements OrganizationSignUpCommandFailure {
        @Override
        public String messageKey() {
            return "identity-access.user.error.duplicate-email";
        }
    }

    /**
     * Missing initial role failure.
     */
    record InitialRoleNotFound() implements OrganizationSignUpCommandFailure {
        @Override
        public String messageKey() {
            return "identity-access.organization-sign-up.error.initial-role-not-found";
        }
    }
}
