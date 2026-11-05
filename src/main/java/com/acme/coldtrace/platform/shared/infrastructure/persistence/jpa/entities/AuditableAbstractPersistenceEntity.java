package com.acme.coldtrace.platform.shared.infrastructure.persistence.jpa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

/**
 * Base class for JPA persistence entities that need identity and audit fields.
 * <p>
 * Domain aggregates stay free from persistence annotations. Concrete JPA
 * entities extend this class to inherit the technical identifier and auditing
 * timestamps used by the relational database.
 *
 * @since 1.0
 */
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AuditableAbstractPersistenceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreatedDate
    @Column(nullable = false, updatable = false, columnDefinition = "datetime(6) default current_timestamp(6)")
    private Instant createdAt;

    @LastModifiedDate
    @Column(nullable = false, columnDefinition = "datetime(6) default current_timestamp(6) on update current_timestamp(6)")
    private Instant updatedAt;

    /**
     * Assigns a persistence identifier when mapping an existing domain object
     * back to a JPA entity.
     *
     * @param id persistence identifier; may be {@code null} for new entities
     */
    public void setId(Long id) {
        this.id = id;
    }
}
