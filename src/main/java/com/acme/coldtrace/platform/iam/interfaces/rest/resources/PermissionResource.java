package com.acme.coldtrace.platform.iam.interfaces.rest.resources;

/**
 * Response resource representing a permission.
 *
 * @param id permission identifier
 * @param resource protected resource name
 * @param action action allowed over the resource
 * @param description permission description or translation key
 * @since 1.0
 */
public record PermissionResource(
        Long id,
        String resource,
        String action,
        String description
) {
}
