package com.acme.coldtrace.platform.iam.infrastructure.persistence.jpa.adapters;

import com.acme.coldtrace.platform.iam.domain.model.aggregates.PasswordResetRequest;
import com.acme.coldtrace.platform.iam.domain.repositories.PasswordResetRequestRepository;
import com.acme.coldtrace.platform.iam.infrastructure.persistence.jpa.assemblers.PasswordResetRequestPersistenceAssembler;
import com.acme.coldtrace.platform.iam.infrastructure.persistence.jpa.repositories.PasswordResetRequestPersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * JPA-backed adapter for the password reset request domain repository.
 *
 * @since 1.0
 */
@Repository
public class PasswordResetRequestRepositoryImpl implements PasswordResetRequestRepository {
    private final PasswordResetRequestPersistenceRepository passwordResetRequestPersistenceRepository;

    public PasswordResetRequestRepositoryImpl(
            PasswordResetRequestPersistenceRepository passwordResetRequestPersistenceRepository
    ) {
        this.passwordResetRequestPersistenceRepository = passwordResetRequestPersistenceRepository;
    }

    @Override
    public PasswordResetRequest save(PasswordResetRequest passwordResetRequest) {
        if (passwordResetRequest.getId() == null) {
            var entity = PasswordResetRequestPersistenceAssembler.toPersistenceFromDomain(passwordResetRequest);
            return PasswordResetRequestPersistenceAssembler.toDomainFromPersistence(
                    passwordResetRequestPersistenceRepository.save(entity)
            );
        }

        var entity = passwordResetRequestPersistenceRepository.findById(passwordResetRequest.getId())
                .orElseGet(() -> PasswordResetRequestPersistenceAssembler.toPersistenceFromDomain(passwordResetRequest));
        PasswordResetRequestPersistenceAssembler.copyDomainState(passwordResetRequest, entity);
        return PasswordResetRequestPersistenceAssembler.toDomainFromPersistence(
                passwordResetRequestPersistenceRepository.save(entity)
        );
    }

    @Override
    public Optional<PasswordResetRequest> findByTokenHash(String tokenHash) {
        return passwordResetRequestPersistenceRepository.findByTokenHash(tokenHash)
                .map(PasswordResetRequestPersistenceAssembler::toDomainFromPersistence);
    }
}
