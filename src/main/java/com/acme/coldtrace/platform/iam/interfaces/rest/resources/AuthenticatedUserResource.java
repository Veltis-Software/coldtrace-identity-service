package com.acme.coldtrace.platform.iam.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Response resource returned after successful authentication.
 *
 * @param id user identifier
 * @param uuid generated user code
 * @param organizationUserId generated organization user identifier
 * @param firstName user first name
 * @param lastName user last name
 * @param email user email address
 * @param organizationId organization identifier
 * @param roleId role identifier
 * @param token JWT bearer token
 * @since 1.0
 */
@Schema(
        name = "AuthenticatedUserResponse",
        description = "Authenticated user information with JWT token",
        example = """
                {
                  "id": 1,
                  "uuid": "USR-1",
                  "organizationUserId": 1,
                  "firstName": "Mauricio",
                  "lastName": "Pajes",
                  "email": "operator@coldtrace.test",
                  "organizationId": 2,
                  "roleId": 1,
                  "token": "eyJhbGciOiJIUzUxMiJ9..."
                }
                """
)
public record AuthenticatedUserResource(
        @Schema(description = "User identifier", example = "1")
        Long id,

        @Schema(description = "Generated user code", example = "USR-1")
        String uuid,

        @Schema(description = "Organization-scoped user identifier", example = "1")
        Long organizationUserId,

        @Schema(description = "User first name", example = "David")
        String firstName,

        @Schema(description = "User last name", example = "Torres")
        String lastName,

        @Schema(description = "User email address", example = "david@coldtrace.example")
        String email,

        @Schema(description = "Organization identifier", example = "1")
        Long organizationId,

        @Schema(description = "Role identifier", example = "1")
        Long roleId,

        @Schema(description = "JWT Bearer token", example = "eyJhbGciOiJIUzI1NiJ9...")
        String token
) {
}
