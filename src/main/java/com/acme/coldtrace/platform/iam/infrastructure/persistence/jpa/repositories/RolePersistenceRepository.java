package com.acme.coldtrace.platform.iam.infrastructure.persistence.jpa.repositories;

import com.acme.coldtrace.platform.iam.domain.model.valueobjects.RoleName;
import com.acme.coldtrace.platform.iam.infrastructure.persistence.jpa.entities.RolePersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for role persistence entities.
 *
 * @since 1.0
 */
@Repository
public interface RolePersistenceRepository extends JpaRepository<RolePersistenceEntity, Long> {
    /**
     * Finds a role by its stable name.
     *
     * @param name role name value object
     * @return persistence entity when found
     */
    Optional<RolePersistenceEntity> findByName(RoleName name);

    /**
     * Checks whether a role exists by stable name.
     *
     * @param name role name value object
     * @return true when the role exists
     */
    boolean existsByName(RoleName name);
}
