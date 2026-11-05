package com.acme.coldtrace.platform.iam.infrastructure.persistence.jpa.adapters;

import com.acme.coldtrace.platform.iam.domain.model.entities.Role;
import com.acme.coldtrace.platform.iam.domain.model.valueobjects.RoleName;
import com.acme.coldtrace.platform.iam.domain.repositories.RoleRepository;
import com.acme.coldtrace.platform.iam.infrastructure.persistence.jpa.assemblers.RolePersistenceAssembler;
import com.acme.coldtrace.platform.iam.infrastructure.persistence.jpa.repositories.RolePersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * JPA-backed adapter for the role domain repository.
 *
 * @since 1.0
 */
@Repository
public class RoleRepositoryImpl implements RoleRepository {
    private final RolePersistenceRepository rolePersistenceRepository;

    public RoleRepositoryImpl(RolePersistenceRepository rolePersistenceRepository) {
        this.rolePersistenceRepository = rolePersistenceRepository;
    }

    @Override
    public List<Role> findAll() {
        return rolePersistenceRepository.findAll().stream()
                .map(RolePersistenceAssembler::toDomainFromPersistence)
                .toList();
    }

    @Override
    public Optional<Role> findByName(String name) {
        return rolePersistenceRepository.findByName(new RoleName(name))
                .map(RolePersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public Optional<Role> findById(Long id) {
        return rolePersistenceRepository.findById(id)
                .map(RolePersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public boolean existsById(Long id) {
        return rolePersistenceRepository.existsById(id);
    }

    @Override
    public boolean existsByName(String name) {
        return rolePersistenceRepository.existsByName(new RoleName(name));
    }

    @Override
    public Role save(Role role) {
        if (role.getId() == null) {
            var entity = RolePersistenceAssembler.toPersistenceFromDomain(role);
            return RolePersistenceAssembler.toDomainFromPersistence(rolePersistenceRepository.save(entity));
        }

        var entity = rolePersistenceRepository.findById(role.getId())
                .orElseGet(() -> RolePersistenceAssembler.toPersistenceFromDomain(role));
        RolePersistenceAssembler.copyDomainState(role, entity);
        return RolePersistenceAssembler.toDomainFromPersistence(rolePersistenceRepository.save(entity));
    }
}
