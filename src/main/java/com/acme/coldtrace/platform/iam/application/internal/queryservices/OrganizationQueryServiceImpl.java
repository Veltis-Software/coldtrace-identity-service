package com.acme.coldtrace.platform.iam.application.internal.queryservices;

import com.acme.coldtrace.platform.iam.application.queryservices.OrganizationQueryService;
import com.acme.coldtrace.platform.iam.domain.model.aggregates.Organization;
import com.acme.coldtrace.platform.iam.domain.model.queries.GetAllOrganizationsQuery;
import com.acme.coldtrace.platform.iam.domain.repositories.OrganizationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Application service implementation for organization query operations.
 *
 * @since 1.0
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class OrganizationQueryServiceImpl implements OrganizationQueryService {
    private final OrganizationRepository organizationRepository;

    public OrganizationQueryServiceImpl(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

    /**
     * Retrieves all organizations from persistence.
     *
     * @param query query object representing the all-organizations request
     * @return list of organizations, possibly empty
     * @see GetAllOrganizationsQuery
     */
    @Override
    public List<Organization> handle(GetAllOrganizationsQuery query) {
        log.debug("Querying all organizations");
        var organizations = organizationRepository.findAll();
        log.debug("Found {} organizations", organizations.size());
        return organizations;
    }
}
