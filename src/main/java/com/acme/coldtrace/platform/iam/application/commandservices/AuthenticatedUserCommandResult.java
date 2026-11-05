package com.acme.coldtrace.platform.iam.application.commandservices;

import com.acme.coldtrace.platform.iam.domain.model.aggregates.User;

/**
 * Application result returned after successful authentication.
 *
 * @param user authenticated user
 * @param token issued bearer token
 * @since 1.0
 */
public record AuthenticatedUserCommandResult(User user, String token) {
}
