package com.acme.coldtrace.platform.iam.domain.model.commands;

/**
 * Command for authenticating a user by email and password.
 *
 * @param email user email address
 * @param password raw password submitted by the client
 * @since 1.0
 */
public record SignInCommand(String email, String password) {
    public SignInCommand {
        email = requireNonBlank(email, "identity-access.authentication.error.email.required").toLowerCase();
        if (!email.contains("@")) {
            throw new IllegalArgumentException("identity-access.authentication.error.email.invalid");
        }
        password = requireNonBlank(password, "identity-access.authentication.error.password.required");
    }

    private static String requireNonBlank(String value, String messageKey) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(messageKey);
        }
        return value.trim();
    }
}
