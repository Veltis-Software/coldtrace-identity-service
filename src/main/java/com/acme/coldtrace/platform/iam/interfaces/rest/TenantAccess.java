package com.acme.coldtrace.platform.iam.interfaces.rest;

import com.acme.coldtrace.platform.iam.domain.repositories.RoleRepository;
import com.acme.coldtrace.platform.iam.domain.repositories.UserRepository;
import com.acme.coldtrace.platform.iam.interfaces.acl.SessionContextFacade;
import com.acme.coldtrace.platform.iam.interfaces.acl.SessionContextFacade.SessionContext;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class TenantAccess {
    private final SessionContextFacade sessions;
    private final RoleRepository roles;
    private final UserRepository users;

    public TenantAccess(SessionContextFacade sessions, RoleRepository roles, UserRepository users) {
        this.sessions = sessions;
        this.roles = roles;
        this.users = users;
    }

    SessionContext session(Authentication authentication) {
        return sessions.resolve(authentication == null ? null : authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    }

    void requireOrganization(Authentication authentication, Long organizationId) {
        requireOrganization(session(authentication), organizationId);
    }

    private void requireOrganization(SessionContext actor, Long organizationId) {
        if (!actor.organizationId().equals(organizationId)) forbidden();
    }

    void requireUserChange(Authentication authentication, Long organizationId, Long userId, Long roleId) {
        var actor = session(authentication);
        requireOrganization(actor, organizationId);
        if (!actor.permissions().contains("users:manage")) forbidden();
        if (actor.userId().equals(userId)) forbidden();
        if (userId != null) {
            users.findByIdAndOrganizationId(userId, organizationId)
                    .ifPresent(user -> requireManageableRole(actor, user.getRoleId()));
        }
        if (roleId != null) requireManageableRole(actor, roleId);
    }

    private void requireManageableRole(SessionContext actor, Long roleId) {
        roles.findById(roleId).ifPresent(role -> {
            if ("super-admin".equals(role.getName()) ||
                    ("administrator".equals(role.getName()) && !actor.permissions().contains("administrators:manage"))) {
                forbidden();
            }
        });
    }

    private static void forbidden() {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    }
}
