package com.acme.coldtrace.platform.iam.domain.model.commands;

/**
 * Command for creating a user.
 *
 * @param firstName user first name
 * @param lastName user last name
 * @param email user email address
 * @param password raw password used to create credentials
 * @param organizationId organization identifier
 * @param roleId role identifier
 * @since 1.0
 */
public record CreateUserCommand(
        String firstName,
        String lastName,
        String email,
        String password,
        Long organizationId,
        Long roleId
) {
    /**
     * Validates and normalizes user creation data.
     *
     * @throws IllegalArgumentException if required fields or references are invalid
     */
    public CreateUserCommand {
        firstName = requireNonBlank(firstName, "identity-access.user.error.firstName.required");
        lastName = lastName == null ? "" : lastName.trim();
        email = requireNonBlank(email, "identity-access.user.error.email.required").toLowerCase();
        if (!email.contains("@")) {
            throw new IllegalArgumentException("identity-access.user.error.email.invalid");
        }
        password = requireNonBlank(password, "identity-access.user.error.password.required");
        if (password.length() < 8) {
            throw new IllegalArgumentException("identity-access.user.error.password.invalid");
        }
        if (organizationId == null || organizationId <= 0) {
            throw new IllegalArgumentException("identity-access.user.error.organizationId.invalid");
        }
        if (roleId == null || roleId <= 0) {
            throw new IllegalArgumentException("identity-access.user.error.roleId.invalid");
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
}
