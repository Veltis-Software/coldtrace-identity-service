package com.acme.coldtrace.platform.iam.domain.model.valueobjects;

import com.acme.coldtrace.platform.iam.domain.exceptions.InvalidIamValueException;

import jakarta.validation.constraints.Email;

/**
 * Value object that represents a validated email address.
 * <p>
 * IAM uses email values for organization contacts and users. The
 * value object normalizes the string and prevents blank emails from entering
 * the domain model.
 *
 * @param value email address string
 * @since 1.0
 */
public record EmailAddress(@Email String value) {
    /**
     * Creates a normalized email address.
     *
     * @param value email address string
     * @throws InvalidIamValueException when the value is null or blank
     */
    public EmailAddress {
        if (value == null || value.isBlank()) {
            throw new InvalidIamValueException("Email address must not be null or blank");
        }
        value = value.trim();
    }

    /**
     * Returns the email address string.
     *
     * @return email address string
     */
    public String getValue() {
        return value;
    }
}
