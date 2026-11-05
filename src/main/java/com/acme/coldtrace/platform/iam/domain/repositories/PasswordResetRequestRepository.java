package com.acme.coldtrace.platform.iam.domain.repositories;

import com.acme.coldtrace.platform.iam.domain.model.aggregates.PasswordResetRequest;

import java.util.Optional;

/**
 * Domain repository for password reset requests.
 *
 * @since 1.0
 */
public interface PasswordResetRequestRepository {
    /**
     * Saves a password reset request.
     *
     * @param passwordResetRequest request aggregate
     * @return saved aggregate
     */
    PasswordResetRequest save(PasswordResetRequest passwordResetRequest);

    /**
     * Finds a password reset request by token hash.
     *
     * @param tokenHash token hash
     * @return request when found
     */
    Optional<PasswordResetRequest> findByTokenHash(String tokenHash);
}
