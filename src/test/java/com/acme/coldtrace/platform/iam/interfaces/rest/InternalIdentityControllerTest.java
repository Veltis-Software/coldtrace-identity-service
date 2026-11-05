package com.acme.coldtrace.platform.iam.interfaces.rest;

import com.acme.coldtrace.platform.iam.interfaces.acl.IamContextFacade;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.web.server.ResponseStatusException;

class InternalIdentityControllerTest {
  private static final String KEY="isolated-test-workload-key-with-32-characters";
  private final IamContextFacade owner=new IamContextFacade() {
    public boolean organizationExists(Long id){return id==7;}
    public boolean userExistsByIdAndOrganizationId(Long id,Long user){return id==7&&user==9;}
    public int countUsersByOrganizationId(Long id){return id==7?2:0;}
    public List<Long> fetchOrganizationIds(){return List.of(7L);}
  };
  @Test void missingAndWrongWorkloadKeysCannotReadOwnerData() {
    var controller=new InternalIdentityController(owner,KEY);
    assertEquals(401,assertThrows(ResponseStatusException.class,()->controller.organizations(null)).getStatusCode().value());
    assertEquals(401,assertThrows(ResponseStatusException.class,()->controller.count(7L,"Bearer browser-token")).getStatusCode().value());
  }
  @Test void authenticatedOwnerQueriesPreserveOrganizationScope() {
    var controller=new InternalIdentityController(owner,KEY);
    assertTrue(controller.exists(7L,KEY));assertFalse(controller.exists(8L,KEY));
    assertTrue(controller.user(7L,9L,KEY));assertFalse(controller.user(8L,9L,KEY));
    assertEquals(2,controller.count(7L,KEY));assertEquals(List.of(7L),controller.organizations(KEY));
  }
}
