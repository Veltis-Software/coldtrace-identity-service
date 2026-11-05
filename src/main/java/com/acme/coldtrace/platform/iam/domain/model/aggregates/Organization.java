package com.acme.coldtrace.platform.iam.domain.model.aggregates;

import com.acme.coldtrace.platform.iam.domain.model.commands.CreateOrganizationCommand;
import com.acme.coldtrace.platform.iam.domain.model.valueobjects.EmailAddress;
import com.acme.coldtrace.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;

/**
 * Organization aggregate root for the IAM context.
 * It represents the company account created during the organization sign-up flow.
 *
 * @since 1.0
 */
@Getter
public class Organization extends AbstractDomainAggregateRoot<Organization> {
    private Long id;
    private String legalName;
    private String commercialName;
    private String taxId;
    private EmailAddress contactEmail;

    protected Organization() {
    }

    /**
     * Creates an organization from a create command.
     *
     * @param command command containing organization legal, commercial, tax, and contact data
     * @see CreateOrganizationCommand
     */
    public Organization(CreateOrganizationCommand command) {
        this.legalName = command.legalName();
        this.commercialName = command.commercialName();
        this.taxId = command.taxId();
        this.contactEmail = new EmailAddress(command.contactEmail());
    }

    /**
     * Rebuilds an organization aggregate from persistence state.
     *
     * @param id organization identifier assigned by persistence
     * @param legalName legal organization name
     * @param commercialName commercial organization name
     * @param taxId optional tax identifier
     * @param contactEmail organization contact email value object
     */
    public Organization(Long id, String legalName, String commercialName, String taxId, EmailAddress contactEmail) {
        this.id = id;
        this.legalName = legalName;
        this.commercialName = commercialName;
        this.taxId = taxId;
        this.contactEmail = contactEmail;
    }

    /**
     * Returns the contact email as a string for application and REST consumers.
     *
     * @return organization contact email
     */
    public String getContactEmail() {
        return this.contactEmail.value();
    }

    /**
     * Returns the strongly typed contact email value object.
     *
     * @return contact email value object
     */
    public EmailAddress getContactEmailValue() {
        return this.contactEmail;
    }
}
