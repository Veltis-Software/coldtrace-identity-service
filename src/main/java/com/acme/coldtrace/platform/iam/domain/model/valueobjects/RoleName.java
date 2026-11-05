package com.acme.coldtrace.platform.iam.domain.model.valueobjects;

import com.acme.coldtrace.platform.iam.domain.exceptions.InvalidIamValueException;

/**
 * Value object for stable role identifiers.
 * <p>
 * Role names are used by frontend authorization metadata and seeded reference
 * data. They are modeled explicitly so a role cannot be created with a blank
 * identifier.
 *
 * @param value stable role identifier
 * @since 1.0
 */
public record RoleName(String value) {
    /**
     * Creates a normalized role name.
     *
     * @param value stable role identifier
     * @throws InvalidIamValueException when the value is null or blank
     */
    public RoleName {
        if (value == null || value.isBlank()) {
            throw new InvalidIamValueException("Role name must not be null or blank");
        }
        value = value.trim();
    }

    /**
     * Returns the role name string.
     *
     * @return stable role identifier
     */
    public String getValue() {
        return value;
    }
}
