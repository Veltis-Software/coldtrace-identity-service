package com.acme.coldtrace.platform.iam.infrastructure.persistence.jpa.embeddables;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

/**
 * Persistence embeddable for role permission values.
 *
 * @since 1.0
 */
@Getter
@Setter
@Embeddable
public class PermissionPersistenceEmbeddable {
    private Long id;
    private String resource;
    private String action;
    private String description;

    protected PermissionPersistenceEmbeddable() {
    }

    /**
     * Creates a permission persistence value.
     *
     * @param id permission identifier used by the frontend contract
     * @param resource protected resource name
     * @param action allowed action
     * @param description translation key for the permission description
     */
    public PermissionPersistenceEmbeddable(Long id, String resource, String action, String description) {
        this.id = id;
        this.resource = resource;
        this.action = action;
        this.description = description;
    }
}
