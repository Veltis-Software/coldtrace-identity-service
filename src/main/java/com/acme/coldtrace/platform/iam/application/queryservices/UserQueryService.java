package com.acme.coldtrace.platform.iam.application.queryservices;

import com.acme.coldtrace.platform.iam.domain.model.aggregates.User;
import com.acme.coldtrace.platform.iam.domain.model.queries.GetUsersByOrganizationIdQuery;
import com.acme.coldtrace.platform.shared.application.result.Result;

import java.util.List;

/**
 * Application service contract providing read access to users.
 *
 * @since 1.0
 */
public interface UserQueryService {
    /**
     * Retrieves users by organization.
     *
     * @param query query object containing the organization identifier
     * @return success with users for the organization, possibly empty, or query failure
     * @see GetUsersByOrganizationIdQuery
     */
    Result<List<User>, UserQueryFailure> handle(GetUsersByOrganizationIdQuery query);
}
