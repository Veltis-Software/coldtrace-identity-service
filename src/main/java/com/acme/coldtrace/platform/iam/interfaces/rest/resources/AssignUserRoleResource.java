package com.acme.coldtrace.platform.iam.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Request resource used to assign a role to an organization user.
 *
 * @param roleId target role identifier
 * @since 1.0
 */
@Schema(
        name = "AssignUserRoleRequest",
        description = "Request payload for assigning or replacing an organization user's role"
)
public record AssignUserRoleResource(
        @NotNull(message = "is required")
        @Positive(message = "must be positive")
        @Schema(description = "Target role identifier", example = "2")
        Long roleId
) {
}
