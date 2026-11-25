package com.acme.coldtrace.platform.iam.interfaces.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.acme.coldtrace.platform.iam.domain.model.entities.Role;
import com.acme.coldtrace.platform.iam.domain.repositories.RoleRepository;
import com.acme.coldtrace.platform.iam.domain.repositories.UserRepository;
import com.acme.coldtrace.platform.iam.interfaces.acl.IamContextFacade;
import com.acme.coldtrace.platform.iam.interfaces.acl.SessionContextFacade;
import com.acme.coldtrace.platform.iam.interfaces.acl.SessionContextFacade.SessionContext;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.server.ResponseStatusException;

public class TenantAccessSteps {
  private static final String KEY = "isolated-test-workload-key-with-32-characters";
  private static final long ACTOR_ID = 9L;

  private SessionContextFacade sessions;
  private RoleRepository roles;
  private TenantAccess access;
  private UsernamePasswordAuthenticationToken actor;
  private String result;
  private InternalIdentityController internal;
  private Integer internalAnswer;

  @Before
  public void prepare() {
    sessions = mock(SessionContextFacade.class);
    roles = mock(RoleRepository.class);
    access = new TenantAccess(sessions, roles, mock(UserRepository.class));
    actor = UsernamePasswordAuthenticationToken.authenticated("operator@example.test", null, List.of());
  }

  @Given("a signed-in user of organization {long} with permissions {string}")
  public void signedIn(long organizationId, String permissions) {
    when(sessions.resolve(actor.getName()))
        .thenReturn(
            Optional.of(
                new SessionContext(
                    ACTOR_ID, organizationId, "Mariana Vargas", Arrays.asList(permissions.split(",")))));
  }

  @Given("no signed-in user")
  public void anonymous() {
    when(sessions.resolve(actor.getName())).thenReturn(Optional.empty());
  }

  @And("the role {long} is {string}")
  public void role(long roleId, String name) {
    when(roles.findById(roleId)).thenReturn(Optional.of(new Role(name, name, List.of())));
  }

  @When("the user accesses organization {long}")
  public void accesses(long organizationId) {
    result = outcome(() -> access.requireOrganization(actor, organizationId));
  }

  @When("the user assigns role {long} to user {long} of organization {long}")
  public void assignToUser(long roleId, long userId, long organizationId) {
    result = outcome(() -> access.requireUserChange(actor, organizationId, userId, roleId));
  }

  @When("the user assigns role {long} to a new user of organization {long}")
  public void assignToNewUser(long roleId, long organizationId) {
    result = outcome(() -> access.requireUserChange(actor, organizationId, null, roleId));
  }

  @When("the user assigns role {long} to itself")
  public void assignToItself(long roleId) {
    result = outcome(() -> access.requireUserChange(actor, 7L, ACTOR_ID, roleId));
  }

  @Then("the access result is {string}")
  public void accessResult(String expected) {
    assertEquals(expected, result);
  }

  @Given("the identity owner knows organization {long} with {int} users")
  public void owner(long organizationId, int users) {
    internal =
        new InternalIdentityController(
            new IamContextFacade() {
              public boolean organizationExists(Long id) {
                return id == organizationId;
              }

              public boolean userExistsByIdAndOrganizationId(Long id, Long user) {
                return id == organizationId;
              }

              public int countUsersByOrganizationId(Long id) {
                return id == organizationId ? users : 0;
              }

              public List<Long> fetchOrganizationIds() {
                return List.of(organizationId);
              }
            },
            KEY);
  }

  @When("an internal client queries organization {long} users count with the workload key")
  public void internalCount(long organizationId) {
    internalAnswer = internal.count(organizationId, KEY);
  }

  @Then("the internal answer is {int}")
  public void internalAnswer(int expected) {
    assertEquals(expected, internalAnswer);
  }

  @And("a query with a browser token is rejected as unauthorized")
  public void browserTokenRejected() {
    var error =
        assertThrows(ResponseStatusException.class, () -> internal.count(7L, "Bearer browser-token"));
    assertEquals(401, error.getStatusCode().value());
  }

  private static String outcome(Runnable action) {
    try {
      action.run();
      return "allowed";
    } catch (ResponseStatusException error) {
      return switch (error.getStatusCode().value()) {
        case 401 -> "unauthorized";
        case 403 -> "forbidden";
        default -> "status " + error.getStatusCode().value();
      };
    }
  }
}
