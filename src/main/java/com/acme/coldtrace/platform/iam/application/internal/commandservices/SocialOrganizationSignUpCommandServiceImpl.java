package com.acme.coldtrace.platform.iam.application.internal.commandservices;

import com.acme.coldtrace.platform.billing.interfaces.acl.SubscriptionBillingContextFacade;
import com.acme.coldtrace.platform.iam.application.commandservices.AuthenticatedUserCommandResult;
import com.acme.coldtrace.platform.iam.application.commandservices.SocialOrganizationSignUpCommandService;
import com.acme.coldtrace.platform.iam.application.internal.outboundservices.hashing.HashingService;
import com.acme.coldtrace.platform.iam.application.internal.outboundservices.social.ExternalIdentityProviderService;
import com.acme.coldtrace.platform.iam.application.internal.outboundservices.social.ProviderIdentity;
import com.acme.coldtrace.platform.iam.application.internal.outboundservices.tokens.TokenService;
import com.acme.coldtrace.platform.iam.domain.model.aggregates.ExternalIdentity;
import com.acme.coldtrace.platform.iam.domain.model.aggregates.Organization;
import com.acme.coldtrace.platform.iam.domain.model.aggregates.User;
import com.acme.coldtrace.platform.iam.domain.model.commands.CreateOrganizationCommand;
import com.acme.coldtrace.platform.iam.domain.model.commands.CreateUserCommand;
import com.acme.coldtrace.platform.iam.domain.model.commands.SocialOrganizationSignUpCommand;
import com.acme.coldtrace.platform.iam.domain.model.valueobjects.SocialProvider;
import com.acme.coldtrace.platform.iam.domain.repositories.ExternalIdentityRepository;
import com.acme.coldtrace.platform.iam.domain.repositories.OrganizationRepository;
import com.acme.coldtrace.platform.iam.domain.repositories.RoleRepository;
import com.acme.coldtrace.platform.iam.domain.repositories.UserRepository;
import com.acme.coldtrace.platform.shared.application.result.ApplicationError;
import com.acme.coldtrace.platform.shared.application.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Optional;

/**
 * Application service for creating an organization from a verified social identity.
 *
 * @since 1.0
 */
@Slf4j
@Service
public class SocialOrganizationSignUpCommandServiceImpl implements SocialOrganizationSignUpCommandService {
    private static final String INITIAL_ROLE_NAME = "super-admin";

    private final SecureRandom secureRandom = new SecureRandom();
    private final ExternalIdentityProviderService externalIdentityProviderService;
    private final ExternalIdentityRepository externalIdentityRepository;
    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final HashingService hashingService;
    private final TokenService tokenService;
    private final SubscriptionBillingContextFacade subscriptionBillingContextFacade;

    public SocialOrganizationSignUpCommandServiceImpl(
            ExternalIdentityProviderService externalIdentityProviderService,
            ExternalIdentityRepository externalIdentityRepository,
            OrganizationRepository organizationRepository,
            UserRepository userRepository,
            RoleRepository roleRepository,
            HashingService hashingService,
            TokenService tokenService,
            SubscriptionBillingContextFacade subscriptionBillingContextFacade
    ) {
        this.externalIdentityProviderService = externalIdentityProviderService;
        this.externalIdentityRepository = externalIdentityRepository;
        this.organizationRepository = organizationRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.hashingService = hashingService;
        this.tokenService = tokenService;
        this.subscriptionBillingContextFacade = subscriptionBillingContextFacade;
    }

    /**
     * Creates an organization and its first social-authenticated user.
     *
     * @param command command containing provider token and organization profile data
     * @return authenticated user result or controlled application error
     */
    @Override
    @Transactional
    public Result<AuthenticatedUserCommandResult, ApplicationError> handle(SocialOrganizationSignUpCommand command) {
        var providerIdentityResult = externalIdentityProviderService.validate(command.toSocialSignInCommand());
        if (providerIdentityResult.isFailure()) {
            return Result.failure(providerIdentityResult.failure().orElseThrow());
        }

        var providerIdentity = providerIdentityResult.success().orElseThrow();
        var linkedUser = findLinkedUser(providerIdentity);
        if (linkedUser.isPresent()) {
            log.warn("Social organization sign-up attempted with already linked identity: userId={}, provider={}",
                    linkedUser.get().getId(), providerIdentity.provider().code());
            return duplicateUserEmail();
        }

        if (!hasText(providerIdentity.email())) {
            log.warn("Social organization sign-up requires provider email: provider={}",
                    providerIdentity.provider().code());
            return providerValidationFailed();
        }

        var existingUser = userRepository.findByEmail(providerIdentity.email());
        if (existingUser.isPresent()) {
            log.warn("Social organization sign-up attempted with existing user email: userId={}, email={}",
                    existingUser.get().getId(), providerIdentity.email());
            return duplicateUserEmail();
        }

        if (organizationRepository.existsByContactEmail(providerIdentity.email())) {
            log.warn("Duplicate organization contact email detected during social sign-up: {}",
                    providerIdentity.email());
            return Result.failure(ApplicationError.conflict(
                    "Organization",
                    "identity-access.organization.error.contactEmail.duplicate"
            ));
        }

        var initialRole = roleRepository.findByName(INITIAL_ROLE_NAME);
        if (initialRole.isEmpty()) {
            log.error("Initial sign-up role not found during social sign-up: {}", INITIAL_ROLE_NAME);
            return Result.failure(new ApplicationError(
                    "UNEXPECTED_ERROR",
                    "Initial organization sign-up role was not found",
                    "identity-access.organization-sign-up.error.initial-role-not-found"
            ));
        }

        try {
            var organization = organizationRepository.save(new Organization(new CreateOrganizationCommand(
                    command.organizationName(),
                    command.organizationName(),
                    null,
                    providerIdentity.email()
            )));
            subscriptionBillingContextFacade.initializeBaseSubscriptionForOrganization(organization.getId());
            var nameParts = splitName(command.fullName());
            var userCommand = new CreateUserCommand(
                    nameParts.firstName(),
                    nameParts.lastName(),
                    providerIdentity.email(),
                    generateProviderManagedPassword(),
                    organization.getId(),
                    initialRole.get().getId()
            );
            var user = userRepository.save(new User(userCommand, hashingService.encode(userCommand.password())));
            externalIdentityRepository.save(new ExternalIdentity(
                    providerIdentity.provider(),
                    providerIdentity.subject(),
                    providerIdentity.email(),
                    user.getId()
            ));
            log.info("Social organization sign-up completed: organizationId={}, userId={}, provider={}",
                    organization.getId(), user.getId(), providerIdentity.provider().code());
            return authenticated(user, providerIdentity.provider());
        } catch (DataIntegrityViolationException exception) {
            log.warn("Social organization sign-up conflict: provider={}, reason={}",
                    providerIdentity.provider().code(), exception.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.failure(ApplicationError.conflict(
                    "SocialIdentity",
                    "identity-access.authentication.error.social-identity-conflict"
            ));
        }
    }

    private Optional<User> findLinkedUser(ProviderIdentity providerIdentity) {
        return externalIdentityRepository
                .findByProviderAndProviderSubject(providerIdentity.provider(), providerIdentity.subject())
                .flatMap(identity -> {
                    var user = userRepository.findById(identity.getUserId());
                    if (user.isEmpty()) {
                        log.warn("Social identity points to missing user: externalIdentityId={}, userId={}",
                                identity.getId(), identity.getUserId());
                    }
                    return user;
                });
    }

    private Result<AuthenticatedUserCommandResult, ApplicationError> authenticated(
            User authenticatedUser,
            SocialProvider provider
    ) {
        var token = tokenService.generateToken(authenticatedUser.getEmail());
        log.info("User authenticated after social organization sign-up: userId={}, provider={}",
                authenticatedUser.getId(), provider.code());
        return Result.success(new AuthenticatedUserCommandResult(authenticatedUser, token));
    }

    private Result<AuthenticatedUserCommandResult, ApplicationError> duplicateUserEmail() {
        return Result.failure(ApplicationError.conflict(
                "User",
                "identity-access.user.error.email.duplicate"
        ));
    }

    private Result<AuthenticatedUserCommandResult, ApplicationError> providerValidationFailed() {
        return Result.failure(ApplicationError.providerValidationFailed(
                "identity-access.authentication.error.provider-validation-failed"
        ));
    }

    private NameParts splitName(String fullName) {
        var normalizedName = fullName.trim().replaceAll("\\s+", " ");
        var names = normalizedName.split(" ", 2);
        return new NameParts(names[0], names.length > 1 ? names[1] : "");
    }

    private String generateProviderManagedPassword() {
        var bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private record NameParts(String firstName, String lastName) {
    }
}
