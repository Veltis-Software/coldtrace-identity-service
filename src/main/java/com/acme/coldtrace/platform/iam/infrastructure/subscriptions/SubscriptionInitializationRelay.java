package com.acme.coldtrace.platform.iam.infrastructure.subscriptions;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** Only committed rows are visible; delivery is at least once to an idempotent owner API. */
@Component
public class SubscriptionInitializationRelay {
  private final JdbcTemplate jdbc;
  private final SubscriptionServiceAdapter subscriptions;
  public SubscriptionInitializationRelay(JdbcTemplate jdbc, SubscriptionServiceAdapter subscriptions) {
    this.jdbc=jdbc; this.subscriptions=subscriptions;
  }
  @Scheduled(fixedDelayString="${coldtrace.subscription.relay-delay-ms:5000}")
  public void deliver() {
    var ids=jdbc.queryForList("SELECT organization_id FROM subscription_initialization_outbox WHERE next_attempt_at<=CURRENT_TIMESTAMP(6) ORDER BY organization_id LIMIT 20",Long.class);
    for (long id:ids) {
      try {
        subscriptions.deliverInitialization(id);
        jdbc.update("DELETE FROM subscription_initialization_outbox WHERE organization_id=?",id);
      } catch (org.springframework.web.client.RestClientException error) {
        jdbc.update("UPDATE subscription_initialization_outbox SET attempts=attempts+1,next_attempt_at=TIMESTAMPADD(SECOND,LEAST(300,5*POW(2,LEAST(attempts,6))),CURRENT_TIMESTAMP(6)) WHERE organization_id=?",id);
      }
    }
  }
}
