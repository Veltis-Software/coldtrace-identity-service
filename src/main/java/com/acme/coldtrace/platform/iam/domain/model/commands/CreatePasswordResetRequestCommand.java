package com.acme.coldtrace.platform.iam.domain.model.commands;

import java.util.Locale;

/**
 * Command for requesting a password reset.
 *
 * @param email user email address
 * @since 1.0
 */
public record CreatePasswordResetRequestCommand(String email) {
    public CreatePasswordResetRequestCommand {
        email = requireNonBlank(email, "identity-access.password-reset.error.email.required")
                .toLowerCase(Locale.ROOT);
        if (!email.contains("@")) {
            throw new IllegalArgumentException("identity-access.password-reset.error.email.invalid");
        }
    }

    private static String requireNonBlank(String value, String messageKey) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(messageKey);
        }
        return value.trim();
    }
}
