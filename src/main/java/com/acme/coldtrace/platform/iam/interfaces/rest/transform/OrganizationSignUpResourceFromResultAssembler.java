package com.acme.coldtrace.platform.iam.interfaces.rest.transform;

import com.acme.coldtrace.platform.iam.application.commandservices.OrganizationSignUpCommandResult;
import com.acme.coldtrace.platform.iam.interfaces.rest.resources.OrganizationSignUpResource;

/**
 * Interface layer translator converting organization sign-up results to resources.
 *
 * @since 1.0
 */
public class OrganizationSignUpResourceFromResultAssembler {
    /**
     * Converts an organization sign-up command result into a response resource.
     *
     * @param result organization sign-up application result
     * @return organization sign-up response resource
     */
    public static OrganizationSignUpResource toResourceFromResult(OrganizationSignUpCommandResult result) {
        return new OrganizationSignUpResource(
                OrganizationResourceFromEntityAssembler.toResourceFromEntity(result.organization()),
                UserResourceFromEntityAssembler.toResourceFromEntity(result.user())
        );
    }
}
