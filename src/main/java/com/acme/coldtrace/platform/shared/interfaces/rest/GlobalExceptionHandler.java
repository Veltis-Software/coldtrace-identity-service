package com.acme.coldtrace.platform.shared.interfaces.rest;

import com.acme.coldtrace.platform.shared.application.result.ApplicationError;
import com.acme.coldtrace.platform.shared.interfaces.rest.transform.ErrorResponseAssembler;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * Global exception handler for REST requests.
 * <p>
 * It centralizes interface-layer exception mapping and returns the same
 * {@code ErrorResource} response shape used by explicit application result
 * assemblers. This keeps validation errors produced by {@code @Valid}
 * consistent with business-rule and not-found failures returned by command
 * and query services.
 *
 * @since 1.0
 */
@Slf4j
@NullMarked
@RestControllerAdvice
public class GlobalExceptionHandler {
    private final MessageSource messageSource;

    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    /**
     * Handles request body validation failures raised by Spring MVC.
     *
     * @param exception validation exception
     * @return standardized bad-request error response
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValid(MethodArgumentNotValidException exception) {
        var locale = LocaleContextHolder.getLocale();
        var prefix = messageSource.getMessage("validation.field.prefix", null, "Field", locale);
        var message = messageSource.getMessage(
                "validation.request.failed",
                null,
                "Request validation failed",
                locale
        );
        var details = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> "%s %s: %s".formatted(prefix, error.getField(), error.getDefaultMessage()))
                .collect(Collectors.joining("; "));
        if (details.isBlank()) {
            details = message;
        }
        log.warn("REST request validation failed: {}", details);
        return ErrorResponseAssembler.toErrorResponseFromApplicationError(
                new ApplicationError("VALIDATION_ERROR", message, "request-body: %s".formatted(details))
        );
    }

    /**
     * Handles invalid path, query or conversion arguments.
     *
     * @param exception illegal argument exception
     * @return standardized bad-request error response
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException exception) {
        var locale = LocaleContextHolder.getLocale();
        var messageKey = exception.getMessage() != null
                ? exception.getMessage()
                : "validation.request.failed";
        var message = messageSource.getMessage(
                messageKey,
                null,
                "validation.request.failed",
                locale
        );
        if ("validation.request.failed".equals(message)) {
            message = messageSource.getMessage(
                    "validation.request.failed",
                    null,
                    "Request validation failed",
                    locale
            );
        }
        log.warn("REST request argument rejected: {}", messageKey);
        return ErrorResponseAssembler.toErrorResponseFromApplicationError(
                new ApplicationError("VALIDATION_ERROR", message, "request-argument: %s".formatted(messageKey))
        );
    }
}
