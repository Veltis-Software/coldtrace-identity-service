package com.acme.coldtrace.platform.iam.application.internal.commandservices;

import com.acme.coldtrace.platform.iam.application.commandservices.PasswordResetRequestCommandResult;
import com.acme.coldtrace.platform.iam.application.commandservices.PasswordResetRequestCommandService;
import com.acme.coldtrace.platform.iam.domain.model.aggregates.PasswordResetRequest;
import com.acme.coldtrace.platform.iam.domain.model.commands.CreatePasswordResetRequestCommand;
import com.acme.coldtrace.platform.iam.domain.repositories.PasswordResetRequestRepository;
import com.acme.coldtrace.platform.iam.domain.repositories.UserRepository;
import com.acme.coldtrace.platform.shared.application.result.ApplicationError;
import com.acme.coldtrace.platform.shared.application.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;

/**
 * Application service implementation for password reset request operations.
 *
 * @since 1.0
 */
@Slf4j
@Service
public class PasswordResetRequestCommandServiceImpl implements PasswordResetRequestCommandService {
    private static final int TOKEN_BYTE_LENGTH = 32;
    private static final Duration TOKEN_TTL = Duration.ofMinutes(30);
    private static final String DELIVERY_NOT_CONFIGURED = "EMAIL_DELIVERY_NOT_CONFIGURED";

    private final UserRepository userRepository;
    private final PasswordResetRequestRepository passwordResetRequestRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    public PasswordResetRequestCommandServiceImpl(
            UserRepository userRepository,
            PasswordResetRequestRepository passwordResetRequestRepository
    ) {
        this.userRepository = userRepository;
        this.passwordResetRequestRepository = passwordResetRequestRepository;
    }

    /**
     * Handles a password reset request without revealing whether the email exists.
     *
     * @param command request command
     * @return generic accepted result or controlled application error
     */
    @Override
    @Transactional
    public Result<PasswordResetRequestCommandResult, ApplicationError> handle(CreatePasswordResetRequestCommand command) {
        var requestedAt = Instant.now();
        var expiresAt = requestedAt.plus(TOKEN_TTL);

        var user = userRepository.findByEmail(command.email());
        if (user.isEmpty()) {
            log.info("Password reset requested for a non-existing email");
            return accepted(requestedAt, expiresAt);
        }

        try {
            var rawToken = generateResetToken();
            var tokenHash = hashResetToken(rawToken);
            var request = new PasswordResetRequest(
                    command.email(),
                    user.get().getId(),
                    tokenHash,
                    requestedAt,
                    expiresAt
            );
            passwordResetRequestRepository.save(request);
            log.info("Password reset request accepted: userId={}", user.get().getId());
            return accepted(requestedAt, expiresAt);
        } catch (NoSuchAlgorithmException exception) {
            log.error("Password reset token hashing failed", exception);
            return Result.failure(new ApplicationError(
                    "UNEXPECTED_ERROR",
                    "Password reset request could not be prepared",
                    "identity-access.password-reset.error.request-failed"
            ));
        }
    }

    private Result<PasswordResetRequestCommandResult, ApplicationError> accepted(
            Instant requestedAt,
            Instant expiresAt
    ) {
        return Result.success(new PasswordResetRequestCommandResult(
                true,
                requestedAt,
                expiresAt,
                DELIVERY_NOT_CONFIGURED
        ));
    }

    private String generateResetToken() {
        var bytes = new byte[TOKEN_BYTE_LENGTH];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hashResetToken(String rawToken) throws NoSuchAlgorithmException {
        var digest = MessageDigest.getInstance("SHA-256");
        return HexFormat.of().formatHex(digest.digest(rawToken.getBytes(StandardCharsets.UTF_8)));
    }
}
