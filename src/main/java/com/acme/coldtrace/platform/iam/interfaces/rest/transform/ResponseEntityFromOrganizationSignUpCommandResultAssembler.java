package com.acme.coldtrace.platform.iam.interfaces.rest.transform;

import com.acme.coldtrace.platform.iam.application.commandservices.OrganizationSignUpCommandFailure;
import com.acme.coldtrace.platform.iam.application.commandservices.OrganizationSignUpCommandResult;
import com.acme.coldtrace.platform.shared.application.result.Result;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

import static org.springframework.http.HttpStatus.CREATED;

/**
 * Interface layer translator converting organization sign-up command results to HTTP responses.
 *
 * <p>The assembler keeps the controller focused on request orchestration while
 * centralizing response construction for the onboarding use case. Success maps
 * to the created organization/user representation, while failures are converted
 * to localized {@link ProblemDetail} payloads with the status expected by the
 * REST contract.</p>
 *
 * @since 1.0
 */
public final class ResponseEntityFromOrganizationSignUpCommandResultAssembler {
    private ResponseEntityFromOrganizationSignUpCommandResultAssembler() {
    }

    /**
     * Converts an organization sign-up command result into an HTTP response.
     *
     * @param result organization sign-up command result
     * @param messageSource message source for localized failure details
     * @return 201 response on success or error response on failure
     */
    public static ResponseEntity<?> toResponseEntityFromResult(
            Result<OrganizationSignUpCommandResult, OrganizationSignUpCommandFailure> result,
            MessageSource messageSource
    ) {
        return result.fold(
                signUp -> new ResponseEntity<>(
                        OrganizationSignUpResourceFromResultAssembler.toResourceFromResult(signUp),
                        CREATED
                ),
                failure -> toFailureResponse(failure, messageSource)
        );
    }

    /**
     * Converts an organization sign-up failure into a localized problem detail response.
     *
     * @param failure sign-up command failure
     * @param messageSource message source for i18n
     * @return HTTP response describing the failure
     */
    private static ResponseEntity<ProblemDetail> toFailureResponse(
            OrganizationSignUpCommandFailure failure,
            MessageSource messageSource
    ) {
        var status = statusFromFailure(failure);
        return ResponseEntity.status(status).body(ProblemDetail.forStatusAndDetail(
                status,
                localizeMessage(messageSource, failure)
        ));
    }

    /**
     * Maps an organization sign-up command failure to an HTTP status.
     *
     * @param failure sign-up command failure
     * @return HTTP status for the failure
     */
    private static HttpStatus statusFromFailure(OrganizationSignUpCommandFailure failure) {
        if (failure instanceof OrganizationSignUpCommandFailure.InitialRoleNotFound) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return HttpStatus.CONFLICT;
    }

    /**
     * Resolves the localized message for an organization sign-up command failure.
     *
     * @param messageSource message source for i18n
     * @param failure sign-up command failure
     * @return localized message or message key fallback
     */
    private static String localizeMessage(MessageSource messageSource, OrganizationSignUpCommandFailure failure) {
        return messageSource.getMessage(
                failure.messageKey(),
                failure.args(),
                failure.messageKey(),
                LocaleContextHolder.getLocale()
        );
    }
}
