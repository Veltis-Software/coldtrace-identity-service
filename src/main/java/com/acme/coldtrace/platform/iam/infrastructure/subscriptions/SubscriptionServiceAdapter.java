package com.acme.coldtrace.platform.iam.infrastructure.subscriptions;

import com.acme.coldtrace.platform.billing.interfaces.acl.SubscriptionBillingContextFacade;
import java.time.Duration;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

/** Remote entitlement decisions; subscription initialization joins the IAM transaction. */
@Component
public class SubscriptionServiceAdapter implements SubscriptionBillingContextFacade {
  private final JdbcTemplate jdbc;
  private final RestClient client;

  public SubscriptionServiceAdapter(JdbcTemplate jdbc,
      @Value("${coldtrace.subscription.url}") String url,
      @Value("${coldtrace.internal.service-key}") String key) {
    this.jdbc = jdbc;
    if (key == null || key.length() < 32) throw new IllegalArgumentException("A service key of at least32 characters is required");
    var factory = new org.springframework.http.client.JdkClientHttpRequestFactory(
        java.net.http.HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(2)).build());
    factory.setReadTimeout(Duration.ofSeconds(5));
    client = RestClient.builder().baseUrl(url).requestFactory(factory)
        .defaultHeader("X-Service-Key", key).build();
  }

  @Override
  public void initializeBaseSubscriptionForOrganization(Long organizationId) {
    jdbc.update("INSERT IGNORE INTO subscription_initialization_outbox(organization_id,attempts,next_attempt_at) VALUES (?,0,CURRENT_TIMESTAMP(6))", organizationId);
  }

  @Override
  public Optional<OrganizationEntitlementsSnapshot> fetchEntitlementsByOrganizationId(Long id) {
    try {
      var snapshot = client.get().uri("/internal/organizations/{id}/entitlements", id)
          .retrieve().body(OrganizationEntitlementsSnapshot.class);
      if (snapshot == null || !id.equals(snapshot.organizationId())) throw unavailable(null);
      return Optional.of(snapshot);
    } catch (RestClientException error) {
      throw unavailable(error);
    }
  }

  @Override
  public Optional<EntitlementCheckSnapshot> checkEntitlement(Long id, String entitlement) {
    try {
      var decision=client.get().uri("/internal/organizations/{id}/entitlements/{key}",id,entitlement)
        .retrieve().body(EntitlementCheckSnapshot.class);
      if (decision == null || !id.equals(decision.organizationId())
          || !entitlement.equals(decision.key()) || decision.enabled() == null) throw unavailable(null);
      return Optional.of(decision);
    } catch (RestClientException error) {
      throw unavailable(error);
    }
  }

  @Override
  public boolean canUseEntitlement(Long id, String entitlement) {
    return checkEntitlement(id,entitlement).map(d -> Boolean.TRUE.equals(d.enabled())).orElse(false);
  }

  private static ResponseStatusException unavailable(Exception cause) {
    return new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
        "Subscription decision unavailable", cause);
  }


  public void deliverInitialization(long id) {
    client.put().uri("/internal/organizations/{id}/base-subscription",id).retrieve().toBodilessEntity();
  }
}
