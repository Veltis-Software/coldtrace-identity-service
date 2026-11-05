package com.acme.coldtrace.platform.iam.domain.model.commands;

/**
 * Command for signing up an organization and its first user.
 *
 * @param legalName organization legal name
 * @param commercialName organization commercial name
 * @param taxId optional tax identifier
 * @param contactEmail organization contact email
 * @param firstName first user first name
 * @param lastName first user last name
 * @param email first user email address
 * @param password first user password
 * @since 1.0
 */
public record CreateOrganizationSignUpCommand(
        String legalName,
        String commercialName,
        String taxId,
        String contactEmail,
        String firstName,
        String lastName,
        String email,
        String password
) {
    /**
     * Validates and normalizes organization sign-up data.
     *
     * @throws IllegalArgumentException if required fields are blank or emails are invalid
     */
    public CreateOrganizationSignUpCommand {
        legalName = requireNonBlank(legalName, "identity-access.organization.error.legalName.required");
        commercialName = requireNonBlank(commercialName, "identity-access.organization.error.commercialName.required");
        taxId = normalizeOptional(taxId);
        contactEmail = requireEmail(contactEmail, "identity-access.organization.error.contactEmail.required",
                "identity-access.organization.error.contactEmail.invalid");
        firstName = requireNonBlank(firstName, "identity-access.user.error.firstName.required");
        lastName = lastName == null ? "" : lastName.trim();
        email = requireEmail(email, "identity-access.user.error.email.required",
                "identity-access.user.error.email.invalid");
        password = requireNonBlank(password, "identity-access.user.error.password.required");
        if (password.length() < 8) {
            throw new IllegalArgumentException("identity-access.user.error.password.invalid");
        }
    }

    /**
     * Converts this sign-up command into an organization creation command.
     *
     * @return organization creation command
     */
    public CreateOrganizationCommand toCreateOrganizationCommand() {
        return new CreateOrganizationCommand(legalName, commercialName, taxId, contactEmail);
    }

    /**
     * Converts this sign-up command into a user creation command after the organization exists.
     *
     * @param organizationId created organization identifier
     * @param roleId initial role identifier
     * @return user creation command
     */
    public CreateUserCommand toCreateUserCommand(Long organizationId, Long roleId) {
        return new CreateUserCommand(firstName, lastName, email, password, organizationId, roleId);
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
     * Requires an email-like value and returns it lower-cased.
     *
     * @param value input value
     * @param requiredMessageKey message key used when the value is blank
     * @param invalidMessageKey message key used when the value is invalid
     * @return normalized email value
     */
    private static String requireEmail(String value, String requiredMessageKey, String invalidMessageKey) {
        var email = requireNonBlank(value, requiredMessageKey).toLowerCase();
        if (!email.contains("@")) {
            throw new IllegalArgumentException(invalidMessageKey);
        }
        return email;
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
