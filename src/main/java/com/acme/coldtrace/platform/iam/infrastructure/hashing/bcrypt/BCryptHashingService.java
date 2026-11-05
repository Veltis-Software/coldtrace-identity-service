package com.acme.coldtrace.platform.iam.infrastructure.hashing.bcrypt;

import com.acme.coldtrace.platform.iam.application.internal.outboundservices.hashing.HashingService;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Marker interface for the BCrypt hashing adapter.
 *
 * @since 1.0
 */
public interface BCryptHashingService extends HashingService, PasswordEncoder {
}
