package com.acme.coldtrace.platform.iam.application.internal.queryservices;

import com.acme.coldtrace.platform.iam.application.queryservices.UserQueryFailure;
import com.acme.coldtrace.platform.iam.application.queryservices.UserQueryService;
import com.acme.coldtrace.platform.iam.domain.model.aggregates.User;
import com.acme.coldtrace.platform.iam.domain.model.queries.GetUsersByOrganizationIdQuery;
import com.acme.coldtrace.platform.iam.domain.repositories.OrganizationRepository;
import com.acme.coldtrace.platform.iam.domain.repositories.UserRepository;
import com.acme.coldtrace.platform.shared.application.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Application service implementation for user query operations.
 * <p>
 * User queries are scoped by organization. The service verifies the organization
 * exists before returning users so clients do not receive an empty list for an
 * invalid tenant boundary.
 *
 * @since 1.0
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class UserQueryServiceImpl implements UserQueryService {
    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;

    /**
     * Creates the query service with the repositories required by user read use cases.
     *
     * @param userRepository repository used to read users
     * @param organizationRepository repository used to verify organization existence
     */
    public UserQueryServiceImpl(
            UserRepository userRepository,
            OrganizationRepository organizationRepository
    ) {
        this.userRepository = userRepository;
        this.organizationRepository = organizationRepository;
    }

    /**
     * Retrieves users from persistence by organization.
     *
     * @param query query object containing the organization identifier
     * @return success with users for the organization, possibly empty, or failure when the organization does not exist
     * @see GetUsersByOrganizationIdQuery
     */
    @Override
    public Result<List<User>, UserQueryFailure> handle(GetUsersByOrganizationIdQuery query) {
        log.debug("Querying users by organizationId={}", query.organizationId());

        if (!organizationRepository.existsById(query.organizationId())) {
            log.warn("Organization not found for user query: organizationId={}", query.organizationId());
            return Result.failure(new UserQueryFailure.OrganizationNotFound());
        }

        var users = userRepository.findAllByOrganizationId(query.organizationId());
        log.debug("Found {} users for organizationId={}", users.size(), query.organizationId());
        return Result.success(users);
    }
}
