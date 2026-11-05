package com.acme.coldtrace.platform.iam.application.internal.commandservices;

import com.acme.coldtrace.platform.billing.interfaces.acl.SubscriptionBillingContextFacade;
import com.acme.coldtrace.platform.iam.application.commandservices.OrganizationSignUpCommandFailure;
import com.acme.coldtrace.platform.iam.application.commandservices.OrganizationSignUpCommandResult;
import com.acme.coldtrace.platform.iam.application.commandservices.OrganizationSignUpCommandService;
import com.acme.coldtrace.platform.iam.application.internal.outboundservices.hashing.HashingService;
import com.acme.coldtrace.platform.iam.domain.model.aggregates.Organization;
import com.acme.coldtrace.platform.iam.domain.model.aggregates.User;
import com.acme.coldtrace.platform.iam.domain.model.commands.CreateOrganizationSignUpCommand;
import com.acme.coldtrace.platform.iam.domain.repositories.OrganizationRepository;
import com.acme.coldtrace.platform.iam.domain.repositories.RoleRepository;
import com.acme.coldtrace.platform.iam.domain.repositories.UserRepository;
import com.acme.coldtrace.platform.shared.application.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Application service implementation for organization sign-up.
 * It creates the organization and the first user in a single transaction.
 *
 * @since 1.0
 */
@Slf4j
@Service
public class OrganizationSignUpCommandServiceImpl implements OrganizationSignUpCommandService {
    private static final String INITIAL_ROLE_NAME = "super-admin";

    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final HashingService hashingService;
    private final SubscriptionBillingContextFacade subscriptionBillingContextFacade;

    public OrganizationSignUpCommandServiceImpl(
            OrganizationRepository organizationRepository,
            UserRepository userRepository,
            RoleRepository roleRepository,
            HashingService hashingService,
            SubscriptionBillingContextFacade subscriptionBillingContextFacade
    ) {
        this.organizationRepository = organizationRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.hashingService = hashingService;
        this.subscriptionBillingContextFacade = subscriptionBillingContextFacade;
    }

    /**
     * Handles organization sign-up by creating an organization and its first user.
     *
     * @param command command containing organization and first-user data
     * @return success with both created aggregates or failure with a sign-up command error
     * @see CreateOrganizationSignUpCommand
     */
    @Override
    @Transactional
    public Result<OrganizationSignUpCommandResult, OrganizationSignUpCommandFailure> handle(
            CreateOrganizationSignUpCommand command
    ) {
        if (organizationRepository.existsByContactEmail(command.contactEmail())) {
            log.warn("Duplicate organization contact email detected during sign-up: {}", command.contactEmail());
            return Result.failure(new OrganizationSignUpCommandFailure.DuplicateOrganizationContactEmail());
        }
        if (command.taxId() != null && organizationRepository.existsByTaxId(command.taxId())) {
            log.warn("Duplicate organization tax id detected during sign-up: {}", command.taxId());
            return Result.failure(new OrganizationSignUpCommandFailure.DuplicateOrganizationTaxId());
        }
        if (userRepository.existsByEmail(command.email())) {
            log.warn("Duplicate user email detected during sign-up: {}", command.email());
            return Result.failure(new OrganizationSignUpCommandFailure.DuplicateUserEmail());
        }

        var initialRole = roleRepository.findByName(INITIAL_ROLE_NAME);
        if (initialRole.isEmpty()) {
            log.error("Initial sign-up role not found: {}", INITIAL_ROLE_NAME);
            return Result.failure(new OrganizationSignUpCommandFailure.InitialRoleNotFound());
        }

        var organization = organizationRepository.save(new Organization(command.toCreateOrganizationCommand()));
        subscriptionBillingContextFacade.initializeBaseSubscriptionForOrganization(organization.getId());
        var userCommand = command.toCreateUserCommand(organization.getId(), initialRole.get().getId());
        var user = userRepository.save(new User(userCommand, hashingService.encode(userCommand.password())));

        log.info("Organization sign-up completed: organizationId={}, userId={}, role={}",
                organization.getId(), user.getId(), INITIAL_ROLE_NAME);
        return Result.success(new OrganizationSignUpCommandResult(organization, user));
    }
}
