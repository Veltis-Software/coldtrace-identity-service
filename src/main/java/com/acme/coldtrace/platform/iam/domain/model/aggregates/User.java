package com.acme.coldtrace.platform.iam.domain.model.aggregates;

import com.acme.coldtrace.platform.iam.domain.model.commands.AssignUserRoleCommand;
import com.acme.coldtrace.platform.iam.domain.model.commands.CreateUserCommand;
import com.acme.coldtrace.platform.iam.domain.model.valueobjects.EmailAddress;
import com.acme.coldtrace.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;

/**
 * User aggregate for the IAM context.
 * It represents a person linked to an organization and role.
 *
 * @since 1.0
 */
@Getter
public class User extends AbstractDomainAggregateRoot<User> {
    private Long id;
    private String uuid;
    private Long organizationUserId;
    private String firstName;
    private String lastName;
    private EmailAddress email;
    private String passwordHash;
    private Long organizationId;
    private Long roleId;

    protected User() {
    }

    /**
     * Creates a user from a create command and encoded password.
     *
     * @param command command containing user identity data and organization-role references
     * @param passwordHash encoded password
     */
    public User(CreateUserCommand command, String passwordHash) {
        this.firstName = command.firstName();
        this.lastName = command.lastName();
        this.email = new EmailAddress(command.email());
        this.passwordHash = passwordHash;
        this.organizationId = command.organizationId();
        this.roleId = command.roleId();
    }

    /**
     * Rebuilds a user aggregate from persistence state.
     *
     * @param id user identifier assigned by persistence
     * @param uuid optional external uuid
     * @param organizationUserId optional organization-specific user identifier
     * @param firstName user first name
     * @param lastName user last name
     * @param email user email value object
     * @param passwordHash encoded password
     * @param organizationId organization identifier
     * @param roleId role identifier
     */
    public User(
            Long id,
            String uuid,
            Long organizationUserId,
            String firstName,
            String lastName,
            EmailAddress email,
            String passwordHash,
            Long organizationId,
            Long roleId
    ) {
        this.id = id;
        this.uuid = uuid;
        this.organizationUserId = organizationUserId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.organizationId = organizationId;
        this.roleId = roleId;
    }

    /**
     * Returns the user email as a string for application and REST consumers.
     *
     * @return user email
     */
    public String getEmail() {
        return this.email.value();
    }

    /**
     * Returns the strongly typed user email value object.
     *
     * @return email value object
     */
    public EmailAddress getEmailValue() {
        return this.email;
    }

    /**
     * Assigns a new role to this user.
     * <p>
     * Role existence and organization membership are application concerns
     * validated before this method is invoked. The aggregate only protects its
     * own invariant: a persisted user must always keep a valid role reference.
     *
     * @param command command containing the target role identifier
     * @throws IllegalArgumentException if the role identifier is invalid
     * @see AssignUserRoleCommand
     */
    public void assignRole(AssignUserRoleCommand command) {
        if (command.roleId() == null || command.roleId() <= 0) {
            throw new IllegalArgumentException("identity-access.user.error.roleId.invalid");
        }
        this.roleId = command.roleId();
    }
}
