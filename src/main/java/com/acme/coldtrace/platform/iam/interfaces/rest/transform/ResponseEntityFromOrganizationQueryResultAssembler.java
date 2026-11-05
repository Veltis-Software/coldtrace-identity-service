package com.acme.coldtrace.platform.iam.interfaces.rest.transform;

import com.acme.coldtrace.platform.iam.domain.model.aggregates.Organization;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * Interface layer translator converting organization query results to HTTP responses.
 *
 * @since 1.0
 */
public class ResponseEntityFromOrganizationQueryResultAssembler {
    /**
     * Converts a list of organizations into a 200 HTTP response.
     *
     * @param organizations organization query result
     * @return response entity containing organization resources
     */
    public static ResponseEntity<?> toResponseEntityFromList(List<Organization> organizations) {
        return ResponseEntity.ok(organizations.stream()
                .map(OrganizationResourceFromEntityAssembler::toResourceFromEntity)
                .toList());
    }
}
