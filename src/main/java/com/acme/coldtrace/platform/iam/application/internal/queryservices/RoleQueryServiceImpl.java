package com.acme.coldtrace.platform.iam.application.internal.queryservices;

import com.acme.coldtrace.platform.iam.application.queryservices.RoleQueryService;
import com.acme.coldtrace.platform.iam.domain.model.entities.Role;
import com.acme.coldtrace.platform.iam.domain.model.queries.GetAllRolesQuery;
import com.acme.coldtrace.platform.iam.domain.repositories.RoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Application service implementation for role query operations.
 *
 * @since 1.0
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class RoleQueryServiceImpl implements RoleQueryService {
    private final RoleRepository roleRepository;

    public RoleQueryServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    /**
     * Retrieves all roles from persistence.
     *
     * @param query query object representing the all-roles request
     * @return list of roles, possibly empty
     * @see GetAllRolesQuery
     */
    @Override
    public List<Role> handle(GetAllRolesQuery query) {
        log.debug("Querying all roles");
        var roles = roleRepository.findAll();
        log.debug("Found {} roles", roles.size());
        return roles;
    }
}
