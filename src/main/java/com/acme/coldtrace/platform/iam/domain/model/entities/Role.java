package com.acme.coldtrace.platform.iam.domain.model.entities;

import com.acme.coldtrace.platform.iam.domain.model.valueobjects.RoleName;
import com.acme.coldtrace.platform.iam.domain.model.valueobjects.Permission;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * Role entity for authorization metadata used by the frontend.
 * A role groups permissions and provides stable role names such as
 * {@code super-admin}, {@code administrator}, and {@code operator}.
 *
 * @since 1.0
 */
@Getter
public class Role {
    private Long id;
    private RoleName name;
    private String label;
    private List<Permission> permissions = new ArrayList<>();

    protected Role() {
    }

    /**
     * Creates a role with a display label and permission set.
     *
     * @param name stable role identifier
     * @param label display label for the role
     * @param permissions permissions assigned to the role
     */
    public Role(String name, String label, List<Permission> permissions) {
        this.name = new RoleName(name);
        this.label = label.trim();
        this.permissions = new ArrayList<>(permissions);
    }

    /**
     * Rebuilds a role entity from persistence state.
     *
     * @param id role identifier assigned by persistence
     * @param name stable role name value object
     * @param label display label
     * @param permissions permissions assigned to the role
     */
    public Role(Long id, RoleName name, String label, List<Permission> permissions) {
        this.id = id;
        this.name = name;
        this.label = label;
        this.permissions = new ArrayList<>(permissions);
    }

    /**
     * Returns the role name as a string for application and REST consumers.
     *
     * @return stable role name
     */
    public String getName() {
        return this.name.value();
    }

    /**
     * Returns the strongly typed role name value object.
     *
     * @return role name value object
     */
    public RoleName getNameValue() {
        return this.name;
    }
}
