package com.acme.coldtrace.platform.iam.application.internal.commandservices;

import com.acme.coldtrace.platform.billing.interfaces.acl.SubscriptionBillingContextFacade;
import com.acme.coldtrace.platform.iam.application.commandservices.OrganizationCommandFailure;
import com.acme.coldtrace.platform.iam.application.commandservices.OrganizationCommandService;
import com.acme.coldtrace.platform.iam.domain.model.aggregates.Organization;
import com.acme.coldtrace.platform.iam.domain.model.commands.CreateOrganizationCommand;
import com.acme.coldtrace.platform.iam.domain.repositories.OrganizationRepository;
import com.acme.coldtrace.platform.shared.application.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Application service implementation for organization command operations.
 * It orchestrates organization aggregate creation and delegates persistence to
 * the organization repository.
 *
 * @since 1.0
 */
@Slf4j
@Service
public class OrganizationCommandServiceImpl implements OrganizationCommandService {
    private final OrganizationRepository organizationRepository;
    private final SubscriptionBillingContextFacade subscriptionBillingContextFacade;

    public OrganizationCommandServiceImpl(
            OrganizationRepository organizationRepository,
            SubscriptionBillingContextFacade subscriptionBillingContextFacade
    ) {
        this.organizationRepository = organizationRepository;
        this.subscriptionBillingContextFacade = subscriptionBillingContextFacade;
    }

    /**
     * Handles creation of an organization aggregate.
     *
     * @param command command containing organization data
     * @return success with created organization or failure with an organization command error
     * @throws IllegalArgumentException if command data is invalid
     * @see CreateOrganizationCommand
     */
    @Override
    @Transactional
    public Result<Organization, OrganizationCommandFailure> handle(CreateOrganizationCommand command) {
        if (organizationRepository.existsByContactEmail(command.contactEmail())) {
            log.warn("Duplicate organization contact email detected: {}", command.contactEmail());
            return Result.failure(new OrganizationCommandFailure.DuplicateContactEmail());
        }
        if (command.taxId() != null && organizationRepository.existsByTaxId(command.taxId())) {
            log.warn("Duplicate organization tax id detected: {}", command.taxId());
            return Result.failure(new OrganizationCommandFailure.DuplicateTaxId());
        }
        var organization = organizationRepository.save(new Organization(command));
        subscriptionBillingContextFacade.initializeBaseSubscriptionForOrganization(organization.getId());
        log.info("Organization created: id={}, contactEmail={}", organization.getId(), organization.getContactEmail());
        return Result.success(organization);
    }
}
