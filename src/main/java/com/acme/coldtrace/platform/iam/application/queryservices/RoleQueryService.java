package com.acme.coldtrace.platform.iam.application.queryservices;

import com.acme.coldtrace.platform.iam.domain.model.entities.Role;
import com.acme.coldtrace.platform.iam.domain.model.queries.GetAllRolesQuery;

import java.util.List;

/**
 * Application service contract providing read access to roles.
 *
 * @since 1.0
 */
public interface RoleQueryService {
    /**
     * Retrieves all roles and their permissions.
     *
     * @param query query object representing the all-roles request
     * @return list of roles, possibly empty
     * @see GetAllRolesQuery
     */
    List<Role> handle(GetAllRolesQuery query);
}
