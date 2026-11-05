package com.acme.coldtrace.platform.iam.application.internal.commandservices;

import com.acme.coldtrace.platform.iam.application.commandservices.RoleCommandService;
import com.acme.coldtrace.platform.iam.domain.model.commands.SeedRolesCommand;
import com.acme.coldtrace.platform.iam.domain.model.entities.Role;
import com.acme.coldtrace.platform.iam.domain.model.valueobjects.Permission;
import com.acme.coldtrace.platform.iam.domain.repositories.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementation of {@link RoleCommandService} to handle {@link SeedRolesCommand}.
 */
@Service
public class RoleCommandServiceImpl implements RoleCommandService {
    private static final Permission MANAGE_ADMINISTRATORS = new Permission(
            1L,
            "administrators",
            "manage",
            "roles-permissions.permissions.manage-administrators"
    );
    private static final Permission MANAGE_USERS = new Permission(
            2L,
            "users",
            "manage",
            "roles-permissions.permissions.manage-users"
    );
    private static final Permission MANAGE_ASSETS = new Permission(
            3L,
            "assets",
            "manage",
            "roles-permissions.permissions.manage-assets"
    );
    private static final Permission VIEW_REPORTS = new Permission(
            4L,
            "reports",
            "view",
            "roles-permissions.permissions.view-reports"
    );
    private static final Permission RESOLVE_ALERTS = new Permission(
            5L,
            "alerts",
            "update",
            "roles-permissions.permissions.resolve-alerts"
    );
    private static final Permission MONITOR_ASSETS = new Permission(
            6L,
            "assets",
            "view",
            "roles-permissions.permissions.monitor-assets"
    );
    private static final Permission READ_ONLY = new Permission(
            7L,
            "operations",
            "view",
            "roles-permissions.permissions.read-only"
    );

    private final RoleRepository roleRepository;

    public RoleCommandServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void handle(SeedRolesCommand command) {
        defaultRoles().forEach(this::seedRole);
    }

    private void seedRole(RoleSeed seed) {
        if (!roleRepository.existsByName(seed.name())) {
            roleRepository.save(new Role(seed.name(), seed.label(), seed.permissions()));
        }
    }

    private List<RoleSeed> defaultRoles() {
        return List.of(
                new RoleSeed(
                        "super-admin",
                        "Super Administrator",
                        List.of(
                                MANAGE_ADMINISTRATORS,
                                MANAGE_USERS,
                                MANAGE_ASSETS,
                                VIEW_REPORTS,
                                RESOLVE_ALERTS,
                                MONITOR_ASSETS,
                                READ_ONLY
                        )
                ),
                new RoleSeed(
                        "administrator",
                        "Administrator",
                        List.of(MANAGE_USERS, MANAGE_ASSETS, VIEW_REPORTS, RESOLVE_ALERTS, MONITOR_ASSETS, READ_ONLY)
                ),
                new RoleSeed(
                        "operations-manager",
                        "Operations Manager",
                        List.of(MANAGE_ASSETS, RESOLVE_ALERTS, VIEW_REPORTS)
                ),
                new RoleSeed(
                        "operator",
                        "Operator",
                        List.of(MONITOR_ASSETS, RESOLVE_ALERTS)
                ),
                new RoleSeed(
                        "auditor",
                        "Auditor",
                        List.of(VIEW_REPORTS, READ_ONLY)
                )
        );
    }

    private record RoleSeed(String name, String label, List<Permission> permissions) {
    }
}
