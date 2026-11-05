package com.acme.coldtrace.platform.iam.application.internal.commandservices;

import com.acme.coldtrace.platform.iam.application.commandservices.AuthenticatedUserCommandResult;
import com.acme.coldtrace.platform.iam.application.commandservices.SocialAuthenticationCommandService;
import com.acme.coldtrace.platform.iam.application.internal.outboundservices.social.ExternalIdentityProviderService;
import com.acme.coldtrace.platform.iam.application.internal.outboundservices.tokens.TokenService;
import com.acme.coldtrace.platform.iam.domain.model.aggregates.ExternalIdentity;
import com.acme.coldtrace.platform.iam.domain.model.aggregates.User;
import com.acme.coldtrace.platform.iam.domain.model.commands.SocialSignInCommand;
import com.acme.coldtrace.platform.iam.domain.model.valueobjects.SocialProvider;
import com.acme.coldtrace.platform.iam.domain.repositories.ExternalIdentityRepository;
import com.acme.coldtrace.platform.iam.domain.repositories.UserRepository;
import com.acme.coldtrace.platform.shared.application.result.ApplicationError;
import com.acme.coldtrace.platform.shared.application.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Application service for Google and Apple authentication.
 *
 * @since 1.0
 */
@Slf4j
@Service
public class SocialAuthenticationCommandServiceImpl implements SocialAuthenticationCommandService {
    private final ExternalIdentityProviderService externalIdentityProviderService;
    private final ExternalIdentityRepository externalIdentityRepository;
    private final UserRepository userRepository;
    private final TokenService tokenService;

    public SocialAuthenticationCommandServiceImpl(
            ExternalIdentityProviderService externalIdentityProviderService,
            ExternalIdentityRepository externalIdentityRepository,
            UserRepository userRepository,
            TokenService tokenService
    ) {
        this.externalIdentityProviderService = externalIdentityProviderService;
        this.externalIdentityRepository = externalIdentityRepository;
        this.userRepository = userRepository;
        this.tokenService = tokenService;
    }

    @Override
    @Transactional
    public Result<AuthenticatedUserCommandResult, ApplicationError> handle(SocialSignInCommand command) {
        var providerIdentityResult = externalIdentityProviderService.validate(command);
        if (providerIdentityResult.isFailure()) {
            return Result.failure(providerIdentityResult.failure().orElseThrow());
        }

        var providerIdentity = providerIdentityResult.success().orElseThrow();
        var linkedIdentity = externalIdentityRepository.findByProviderAndProviderSubject(
                providerIdentity.provider(),
                providerIdentity.subject()
        );

        if (linkedIdentity.isPresent()) {
            var user = userRepository.findById(linkedIdentity.get().getUserId());
            if (user.isEmpty()) {
                log.warn("Social identity points to missing user: externalIdentityId={}, userId={}",
                        linkedIdentity.get().getId(), linkedIdentity.get().getUserId());
                return Result.failure(ApplicationError.notFound(
                        "User",
                        linkedIdentity.get().getUserId().toString()
                ));
            }
            return authenticated(user.get(), providerIdentity.provider());
        }

        var user = findAndLinkExistingUser(command, providerIdentity.email(), providerIdentity.subject());
        if (user.isEmpty()) {
            log.info("Social identity requires onboarding: provider={}, emailPresent={}",
                    providerIdentity.provider().code(), providerIdentity.email() != null);
            return Result.failure(ApplicationError.socialIdentityRequiresOnboarding(
                    "identity-access.authentication.error.social-identity-requires-onboarding"
            ));
        }

        return authenticated(user.get(), providerIdentity.provider());
    }

    private Result<AuthenticatedUserCommandResult, ApplicationError> authenticated(
            User authenticatedUser,
            SocialProvider provider
    ) {
        var token = tokenService.generateToken(authenticatedUser.getEmail());
        log.info("User authenticated with social provider: userId={}, provider={}",
                authenticatedUser.getId(), provider.code());
        return Result.success(new AuthenticatedUserCommandResult(authenticatedUser, token));
    }

    private Optional<User> findAndLinkExistingUser(
            SocialSignInCommand command,
            String providerEmail,
            String providerSubject
    ) {
        if (providerEmail == null || providerEmail.isBlank()) {
            return Optional.empty();
        }
        var user = userRepository.findByEmail(providerEmail);
        if (user.isEmpty()) {
            return Optional.empty();
        }
        try {
            externalIdentityRepository.save(new ExternalIdentity(
                    command.provider(),
                    providerSubject,
                    providerEmail,
                    user.get().getId()
            ));
            log.info("Linked social identity to existing user: userId={}, provider={}",
                    user.get().getId(), command.provider().code());
        } catch (DataIntegrityViolationException exception) {
            log.warn("Social identity link already exists after concurrent request: provider={}",
                    command.provider().code());
            return externalIdentityRepository
                    .findByProviderAndProviderSubject(command.provider(), providerSubject)
                    .flatMap(identity -> userRepository.findById(identity.getUserId()));
        }
        return user;
    }
}
