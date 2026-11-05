CREATE TABLE subscription_initialization_outbox (
 organization_id BIGINT NOT NULL PRIMARY KEY,
 attempts INT NOT NULL DEFAULT 0,
 next_attempt_at DATETIME(6) NOT NULL,
 INDEX ix_subscription_initialization_due(next_attempt_at)
);
