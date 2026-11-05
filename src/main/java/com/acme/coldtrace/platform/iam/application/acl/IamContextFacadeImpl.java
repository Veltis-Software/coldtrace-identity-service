package com.acme.coldtrace.platform.iam.application.acl;

import com.acme.coldtrace.platform.iam.domain.repositories.OrganizationRepository;
import com.acme.coldtrace.platform.iam.domain.repositories.UserRepository;
import com.acme.coldtrace.platform.iam.interfaces.acl.IamContextFacade;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Application-layer implementation of {@link IamContextFacade}.
 *
 * @since 1.0
 */
@Service
public class IamContextFacadeImpl implements IamContextFacade {
    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;

    public IamContextFacadeImpl(
            OrganizationRepository organizationRepository,
            UserRepository userRepository
    ) {
        this.organizationRepository = organizationRepository;
        this.userRepository = userRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean organizationExists(Long organizationId) {
        return organizationId != null && organizationRepository.existsById(organizationId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean userExistsByIdAndOrganizationId(Long organizationId, Long userId) {
        return organizationId != null &&
                userId != null &&
                userRepository.findByIdAndOrganizationId(userId, organizationId).isPresent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int countUsersByOrganizationId(Long organizationId) {
        if (organizationId == null) {
            return 0;
        }
        return userRepository.findAllByOrganizationId(organizationId).size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Long> fetchOrganizationIds() {
        return organizationRepository.findAll().stream()
                .map(organization -> organization.getId())
                .toList();
    }
}
