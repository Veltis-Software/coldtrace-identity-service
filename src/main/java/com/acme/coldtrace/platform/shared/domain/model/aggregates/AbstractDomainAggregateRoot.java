package com.acme.coldtrace.platform.shared.domain.model.aggregates;

import org.springframework.data.domain.AbstractAggregateRoot;

import java.util.Collection;

/**
 * Base class for domain aggregate roots.
 * <p>
 * The class keeps domain aggregates independent from JPA annotations while
 * preserving Spring Data's domain event registration support. Persistence
 * identifiers, auditing timestamps and table mappings belong to infrastructure
 * persistence entities, not to domain model classes.
 *
 * @param <T> concrete aggregate root type
 * @since 1.0
 */
public abstract class AbstractDomainAggregateRoot<T extends AbstractDomainAggregateRoot<T>>
        extends AbstractAggregateRoot<T> {
    /**
     * Registers a domain event inside the aggregate.
     *
     * @param event event to publish after persistence succeeds
     */
    protected void registerDomainEvent(Object event) {
        super.registerEvent(event);
    }

    /**
     * Returns domain events registered since the previous publication.
     * <p>
     * Repository adapters expose and publish these events after saving the
     * corresponding persistence entity. This public method intentionally keeps
     * event publication outside of the aggregate itself.
     *
     * @return registered domain events
     */
    @Override
    public Collection<Object> domainEvents() {
        return super.domainEvents();
    }

    /**
     * Clears already published domain events.
     */
    @Override
    public void clearDomainEvents() {
        super.clearDomainEvents();
    }
}
