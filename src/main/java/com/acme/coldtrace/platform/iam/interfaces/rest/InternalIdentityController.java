package com.acme.coldtrace.platform.iam.interfaces.rest;

import com.acme.coldtrace.platform.iam.interfaces.acl.IamContextFacade;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

/** Published owner queries; the internal workload key never grants a browser session. */
@RestController
@RequestMapping("/internal/identity")
public class InternalIdentityController {
  private final IamContextFacade iam;
  private final byte[] key;
  public InternalIdentityController(IamContextFacade iam,@Value("${coldtrace.internal.service-key}") String key) {
    if(key==null||key.length()<32)throw new IllegalArgumentException("Internal service key is required");
    this.iam=iam;this.key=key.getBytes(StandardCharsets.UTF_8);
  }
  private void authorize(String candidate) {
    if(candidate==null||!MessageDigest.isEqual(key,candidate.getBytes(StandardCharsets.UTF_8)))
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
  }
  @GetMapping("/organizations")
  public List<Long> organizations(@RequestHeader(value="X-Service-Key",required=false)String key) {
    authorize(key);return iam.fetchOrganizationIds();
  }
  @GetMapping("/organizations/{id}/exists")
  public boolean exists(@PathVariable Long id,@RequestHeader(value="X-Service-Key",required=false)String key) {
    authorize(key);return iam.organizationExists(id);
  }
  @GetMapping("/organizations/{id}/users/count")
  public int count(@PathVariable Long id,@RequestHeader(value="X-Service-Key",required=false)String key) {
    authorize(key);return iam.countUsersByOrganizationId(id);
  }
  @GetMapping("/organizations/{id}/users/{userId}/exists")
  public boolean user(@PathVariable Long id,@PathVariable Long userId,@RequestHeader(value="X-Service-Key",required=false)String key) {
    authorize(key);return iam.userExistsByIdAndOrganizationId(id,userId);
  }
}
