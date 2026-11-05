package com.acme.coldtrace.platform.iam.domain.model.valueobjects;

import java.util.Arrays;

/**
 * Supported external identity providers for social authentication.
 *
 * @since 1.0
 */
public enum SocialProvider {
    GOOGLE("google"),
    APPLE("apple");

    private final String code;

    SocialProvider(String code) {
        this.code = code;
    }

    public String code() {
        return code;
    }

    public static SocialProvider fromCode(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("identity-access.authentication.error.provider.required");
        }
        var normalizedCode = code.trim().toLowerCase();
        return Arrays.stream(values())
                .filter(provider -> provider.code.equals(normalizedCode))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("identity-access.authentication.error.provider.unsupported"));
    }
}
