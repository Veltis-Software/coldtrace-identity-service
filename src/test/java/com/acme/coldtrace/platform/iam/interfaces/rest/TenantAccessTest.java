package com.acme.coldtrace.platform.iam.interfaces.rest;

import com.acme.coldtrace.platform.iam.domain.model.entities.Role;
import com.acme.coldtrace.platform.iam.domain.repositories.RoleRepository;
import com.acme.coldtrace.platform.iam.domain.repositories.UserRepository;
import com.acme.coldtrace.platform.iam.interfaces.acl.SessionContextFacade;
import com.acme.coldtrace.platform.iam.interfaces.acl.SessionContextFacade.SessionContext;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.server.ResponseStatusException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TenantAccessTest {
    private final SessionContextFacade sessions = mock(SessionContextFacade.class);
    private final RoleRepository roles = mock(RoleRepository.class);
    private final TenantAccess access = new TenantAccess(sessions, roles, mock(UserRepository.class));
    private final UsernamePasswordAuthenticationToken actor =
            UsernamePasswordAuthenticationToken.authenticated("operator@example.test", null, List.of());

    private void actorPermissions(String... permissions) {
        when(sessions.resolve(actor.getName())).thenReturn(Optional.of(new SessionContext(9L, 7L, "Mariana Vargas", List.of(permissions))));
    }

    @Test void authenticatedUserCanReadOnlyItsOwnOrganization() {
        actorPermissions("assets:view");
        assertDoesNotThrow(() -> access.requireOrganization(actor, 7L));
        assertEquals(403, assertThrows(ResponseStatusException.class, () -> access.requireOrganization(actor, 8L)).getStatusCode().value());
    }

    @Test void userManagerCannotAssignAnAdministratorRoleOrChangeItsOwnRole() {
        actorPermissions("users:manage");
        var administrator = new Role("administrator", "Administrator", List.of());
        when(roles.findById(2L)).thenReturn(Optional.of(administrator));
        assertEquals(403, assertThrows(ResponseStatusException.class, () -> access.requireUserChange(actor, 7L, null, 2L)).getStatusCode().value());
        assertEquals(403, assertThrows(ResponseStatusException.class, () -> access.requireUserChange(actor, 7L, 9L, 3L)).getStatusCode().value());
        actorPermissions("users:manage", "administrators:manage");
        assertDoesNotThrow(() -> access.requireUserChange(actor, 7L, null, 2L));
    }
}
