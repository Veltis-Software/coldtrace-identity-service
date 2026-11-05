package com.acme.coldtrace.platform.iam.interfaces.rest.transform;

import com.acme.coldtrace.platform.iam.application.queryservices.UserQueryFailure;
import com.acme.coldtrace.platform.iam.domain.model.aggregates.User;
import com.acme.coldtrace.platform.shared.application.result.Result;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * Interface-layer assembler that converts user query results into HTTP responses.
 * <p>
 * User query services return either organization-scoped users or explicit failures.
 * This assembler keeps controllers thin by translating those outcomes into resource
 * lists or localized {@link ProblemDetail} payloads.
 *
 * @since 1.0
 */
public final class ResponseEntityFromUserQueryResultAssembler {
    private ResponseEntityFromUserQueryResultAssembler() {
    }

    /**
     * Converts a user list query result into an HTTP response.
     *
     * @param result user query result
     * @param messageSource message source for localized failure details
     * @return response entity containing user resources or a localized problem response
     */
    public static ResponseEntity<?> toResponseEntityFromList(
            Result<List<User>, UserQueryFailure> result,
            MessageSource messageSource
    ) {
        return result.fold(
                users -> ResponseEntity.ok(users.stream()
                        .map(UserResourceFromEntityAssembler::toResourceFromEntity)
                        .toList()),
                failure -> toFailureResponse(failure, messageSource)
        );
    }

    /**
     * Converts a user query failure into a localized {@link ProblemDetail} response.
     *
     * @param failure query failure returned by the application service
     * @param messageSource message source for localized failure details
     * @return response entity with the status that corresponds to the failure
     */
    private static ResponseEntity<?> toFailureResponse(
            UserQueryFailure failure,
            MessageSource messageSource
    ) {
        var status = statusFromFailure(failure);
        return ResponseEntity.status(status).body(ProblemDetail.forStatusAndDetail(
                status,
                localizeMessage(messageSource, failure)
        ));
    }

    /**
     * Maps a query failure to its HTTP status.
     *
     * @param failure user query failure
     * @return HTTP status for the failure
     */
    private static HttpStatus statusFromFailure(UserQueryFailure failure) {
        if (failure instanceof UserQueryFailure.OrganizationNotFound) {
            return HttpStatus.NOT_FOUND;
        }
        return HttpStatus.BAD_REQUEST;
    }

    /**
     * Resolves the localized message for a user query failure.
     *
     * @param messageSource message source for the current request locale
     * @param failure user query failure
     * @return localized message or the message key when no translation exists
     */
    private static String localizeMessage(MessageSource messageSource, UserQueryFailure failure) {
        return messageSource.getMessage(
                failure.messageKey(),
                failure.args(),
                failure.messageKey(),
                LocaleContextHolder.getLocale()
        );
    }
}
