package com.acme.coldtrace.platform.iam.domain.exceptions;

/**
 * Exception raised when an IAM value object receives invalid data.
 *
 * @since 1.0
 */
public class InvalidIamValueException extends IllegalArgumentException {
    /**
     * Creates the exception.
     *
     * @param message validation message or i18n key
     */
    public InvalidIamValueException(String message) {
        super(message);
    }
}
