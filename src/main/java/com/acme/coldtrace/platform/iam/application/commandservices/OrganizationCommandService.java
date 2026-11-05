package com.acme.coldtrace.platform.iam.application.commandservices;

import com.acme.coldtrace.platform.iam.domain.model.aggregates.Organization;
import com.acme.coldtrace.platform.iam.domain.model.commands.CreateOrganizationCommand;
import com.acme.coldtrace.platform.shared.application.result.Result;

/**
 * Application service contract for organization command operations.
 * It exposes write operations used by the interface layer without leaking
 * persistence details into REST controllers.
 *
 * @since 1.0
 */
public interface OrganizationCommandService {
    /**
     * Handles organization creation.
     *
     * @param command command containing organization legal, commercial, and contact data
     * @return success with created organization or failure with an organization command error
     * @throws IllegalArgumentException if the command contains invalid organization data
     * @see CreateOrganizationCommand
     */
    Result<Organization, OrganizationCommandFailure> handle(CreateOrganizationCommand command);
}
