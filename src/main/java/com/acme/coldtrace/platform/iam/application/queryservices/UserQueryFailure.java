package com.acme.coldtrace.platform.iam.application.queryservices;

/**
 * Failure types returned by user query application services.
 * <p>
 * Query failures are explicit so REST assemblers can map recoverable read errors
 * to localized API responses without throwing infrastructure exceptions from
 * controllers.
 *
 * @since 1.0
 */
public sealed interface UserQueryFailure permits UserQueryFailure.OrganizationNotFound {
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
     * Failure returned when the requested organization does not exist.
     */
    record OrganizationNotFound() implements UserQueryFailure {
        @Override
        public String messageKey() {
            return "identity-access.user.error.organization-not-found";
        }
    }
}
