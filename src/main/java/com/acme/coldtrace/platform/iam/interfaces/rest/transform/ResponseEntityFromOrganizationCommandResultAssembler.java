package com.acme.coldtrace.platform.iam.interfaces.rest.transform;

import com.acme.coldtrace.platform.iam.application.commandservices.OrganizationCommandFailure;
import com.acme.coldtrace.platform.iam.domain.model.aggregates.Organization;
import com.acme.coldtrace.platform.shared.application.result.Result;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

import static org.springframework.http.HttpStatus.CREATED;

/**
 * Interface layer translator converting organization command results to HTTP responses.
 *
 * @since 1.0
 */
public class ResponseEntityFromOrganizationCommandResultAssembler {
    /**
     * Converts an organization command result into an HTTP response.
     *
     * @param result organization command result
     * @param messageSource message source for localized failure details
     * @return 201 response on success or error response on failure
     */
    public static ResponseEntity<?> toResponseEntityFromResult(
            Result<Organization, OrganizationCommandFailure> result,
            MessageSource messageSource
    ) {
        return result.fold(
                organization -> new ResponseEntity<>(
                        OrganizationResourceFromEntityAssembler.toResourceFromEntity(organization),
                        CREATED
                ),
                failure -> {
                    var status = statusFromFailure(failure);
                    return ResponseEntity.status(status).body(ProblemDetail.forStatusAndDetail(
                            status,
                            localizeMessage(messageSource, failure)
                    ));
                }
        );
    }

    /**
     * Maps an organization command failure to an HTTP status.
     *
     * @param failure organization command failure
     * @return HTTP status for the failure
     */
    private static HttpStatus statusFromFailure(OrganizationCommandFailure failure) {
        if (failure instanceof OrganizationCommandFailure.DuplicateContactEmail ||
                failure instanceof OrganizationCommandFailure.DuplicateTaxId) {
            return HttpStatus.CONFLICT;
        }
        return HttpStatus.BAD_REQUEST;
    }

    /**
     * Resolves the localized message for an organization command failure.
     *
     * @param messageSource message source for i18n
     * @param failure organization command failure
     * @return localized message or message key fallback
     */
    private static String localizeMessage(MessageSource messageSource, OrganizationCommandFailure failure) {
        return messageSource.getMessage(
                failure.messageKey(),
                failure.args(),
                failure.messageKey(),
                LocaleContextHolder.getLocale()
        );
    }
}
