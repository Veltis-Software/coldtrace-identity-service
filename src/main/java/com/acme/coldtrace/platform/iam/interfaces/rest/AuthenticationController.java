package com.acme.coldtrace.platform.iam.interfaces.rest;

import com.acme.coldtrace.platform.iam.application.commandservices.SocialIdentityProfileCommandService;
import com.acme.coldtrace.platform.iam.application.commandservices.SocialAuthenticationCommandService;
import com.acme.coldtrace.platform.iam.application.commandservices.SocialOrganizationSignUpCommandService;
import com.acme.coldtrace.platform.iam.application.commandservices.UserCommandService;
import com.acme.coldtrace.platform.iam.interfaces.rest.resources.AuthenticatedUserResource;
import com.acme.coldtrace.platform.iam.interfaces.rest.resources.SignInResource;
import com.acme.coldtrace.platform.iam.interfaces.rest.resources.SocialIdentityProfileResource;
import com.acme.coldtrace.platform.iam.interfaces.rest.resources.SocialOrganizationSignUpResource;
import com.acme.coldtrace.platform.iam.interfaces.rest.resources.SocialTokenExchangeResource;
import com.acme.coldtrace.platform.iam.interfaces.rest.transform.AuthenticatedUserResourceFromEntityAssembler;
import com.acme.coldtrace.platform.iam.interfaces.rest.transform.SignInCommandFromResourceAssembler;
import com.acme.coldtrace.platform.iam.interfaces.rest.transform.SocialIdentityProfileResourceFromResultAssembler;
import com.acme.coldtrace.platform.iam.interfaces.rest.transform.SocialOrganizationSignUpCommandFromResourceAssembler;
import com.acme.coldtrace.platform.iam.interfaces.rest.transform.SocialSignInCommandFromResourceAssembler;
import com.acme.coldtrace.platform.shared.interfaces.rest.resources.ErrorResource;
import com.acme.coldtrace.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * REST controller exposing authentication endpoints.
 *
 * @since 1.0
 */
@Slf4j
@RestController
@RequestMapping(value = "/api/v1/authentication", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Authentication", description = "Authentication endpoints")
public class AuthenticationController {
    private final UserCommandService userCommandService;
    private final SocialAuthenticationCommandService socialAuthenticationCommandService;
    private final SocialIdentityProfileCommandService socialIdentityProfileCommandService;
    private final SocialOrganizationSignUpCommandService socialOrganizationSignUpCommandService;

    public AuthenticationController(
            UserCommandService userCommandService,
            SocialAuthenticationCommandService socialAuthenticationCommandService,
            SocialIdentityProfileCommandService socialIdentityProfileCommandService,
            SocialOrganizationSignUpCommandService socialOrganizationSignUpCommandService
    ) {
        this.userCommandService = userCommandService;
        this.socialAuthenticationCommandService = socialAuthenticationCommandService;
        this.socialIdentityProfileCommandService = socialIdentityProfileCommandService;
        this.socialOrganizationSignUpCommandService = socialOrganizationSignUpCommandService;
    }

    /**
     * Authenticates a user and returns authenticated user data with JWT token.
     *
     * @param resource sign-in request resource
     * @return authenticated user resource or standardized error response
     */
    @Operation(
            summary = "User sign-in",
            description = "Authenticates an organization user with email and password. Copy the returned token and paste it in Swagger Authorize as bearerAuth before calling protected endpoints.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Existing ColdTrace user credentials",
                    content = @Content(
                            mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = SignInResource.class),
                            examples = @ExampleObject(
                                    name = "Sign in with email and password",
                                    value = """
                                            {
                                              "email": "operator@coldtrace.test",
                                              "password": "ColdTrace123"
                                            }
                                            """
                            ))))
    @SecurityRequirements
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User authenticated successfully",
                    content = @Content(
                            mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AuthenticatedUserResource.class),
                            examples = @ExampleObject(
                                    name = "Authenticated user with JWT",
                                    value = """
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
                            ))),
            @ApiResponse(responseCode = "400", description = "Malformed request",
                    content = @Content(
                            mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResource.class),
                            examples = @ExampleObject(
                                    name = "Validation error",
                                    value = """
                                            {
                                              "code": "VALIDATION_ERROR",
                                              "message": "email must be a valid email",
                                              "details": "iam.authentication.error.validation"
                                            }
                                            """
                            ))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials",
                    content = @Content(
                            mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResource.class),
                            examples = @ExampleObject(
                                    name = "Invalid credentials",
                                    value = """
                                            {
                                              "code": "INVALID_CREDENTIALS",
                                              "message": "Invalid email or password",
                                              "details": "iam.authentication.error.invalid-credentials"
                                            }
                                            """
                            )))
    })
    @PostMapping("/sign-in")
    public ResponseEntity<?> signIn(@Valid @RequestBody SignInResource resource) {
        log.debug("POST /api/v1/authentication/sign-in - email={}", resource.email());
        var command = SignInCommandFromResourceAssembler.toCommandFromResource(resource);
        var result = userCommandService.handle(command);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                authenticated -> AuthenticatedUserResourceFromEntityAssembler.toResourceFromEntity(
                        authenticated.user(),
                        authenticated.token()
                ),
                HttpStatus.OK
        );
    }

    /**
     * Authenticates a user through a supported social provider.
     *
     * @param provider provider code, either google or apple
     * @param resource provider token exchange request
     * @return authenticated user resource or standardized error response
     */
    @Operation(
            summary = "Social provider sign-in",
            description = "Validates a Google or Apple OIDC response server-side, links it to a local ColdTrace user, and returns a JWT token",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Provider ID token or authorization code returned by Google or Apple",
                    content = @Content(
                            mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = SocialTokenExchangeResource.class),
                            examples = @ExampleObject(
                                    name = "Provider token exchange",
                                    value = """
                                            {
                                              "idToken": "eyJhbGciOiJSUzI1NiJ9...",
                                              "authorizationCode": null,
                                              "redirectUri": "https://coldtrace-frontend-liard.vercel.app/identity-access/sign-in",
                                              "nonce": "nonce-123"
                                            }
                                            """
                            ))))
    @SecurityRequirements
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User authenticated successfully",
                    content = @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AuthenticatedUserResource.class))),
            @ApiResponse(responseCode = "400", description = "Malformed request",
                    content = @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResource.class))),
            @ApiResponse(responseCode = "401", description = "Provider validation failed",
                    content = @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResource.class))),
            @ApiResponse(responseCode = "422", description = "Social identity requires organization sign-up or invitation completion",
                    content = @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResource.class))),
            @ApiResponse(responseCode = "503", description = "Provider configuration is missing",
                    content = @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResource.class)))
    })
    @PostMapping("/social/{provider}/token-exchange")
    public ResponseEntity<?> socialSignIn(
            @PathVariable String provider,
            @RequestBody SocialTokenExchangeResource resource
    ) {
        log.debug("POST /api/v1/authentication/social/{}/token-exchange", provider);
        var command = SocialSignInCommandFromResourceAssembler.toCommandFromResource(provider, resource);
        var result = socialAuthenticationCommandService.handle(command);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                authenticated -> AuthenticatedUserResourceFromEntityAssembler.toResourceFromEntity(
                        authenticated.user(),
                        authenticated.token()
                ),
                HttpStatus.OK
        );
    }

    /**
     * Validates a provider payload and returns profile data for onboarding.
     *
     * @param provider provider code, either google or apple
     * @param resource provider token exchange request
     * @return social profile preview or standardized error response
     */
    @Operation(
            summary = "Social provider profile preview",
            description = "Validates a Google or Apple OIDC response server-side and returns verified profile data to prefill organization sign-up",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Provider ID token or authorization code returned by Google or Apple",
                    content = @Content(
                            mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = SocialTokenExchangeResource.class))))
    @SecurityRequirements
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Social profile validated successfully",
                    content = @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = SocialIdentityProfileResource.class))),
            @ApiResponse(responseCode = "400", description = "Malformed request",
                    content = @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResource.class))),
            @ApiResponse(responseCode = "401", description = "Provider validation failed",
                    content = @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResource.class))),
            @ApiResponse(responseCode = "503", description = "Provider configuration is missing",
                    content = @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResource.class)))
    })
    @PostMapping("/social/{provider}/profile-preview")
    public ResponseEntity<?> socialProfilePreview(
            @PathVariable String provider,
            @RequestBody SocialTokenExchangeResource resource
    ) {
        log.debug("POST /api/v1/authentication/social/{}/profile-preview", provider);
        var command = SocialSignInCommandFromResourceAssembler.toCommandFromResource(provider, resource);
        var result = socialIdentityProfileCommandService.handle(command);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                SocialIdentityProfileResourceFromResultAssembler::toResourceFromResult,
                HttpStatus.OK
        );
    }

    /**
     * Signs up an organization through a supported social provider.
     *
     * @param provider provider code, either google or apple
     * @param resource provider token and organization profile data
     * @return authenticated user resource or standardized error response
     */
    @Operation(
            summary = "Social provider organization sign-up",
            description = "Validates a Google or Apple OIDC response server-side, creates the organization and first user when needed, links the provider identity, and returns a JWT token",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Provider token and organization onboarding data",
                    content = @Content(
                            mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = SocialOrganizationSignUpResource.class),
                            examples = @ExampleObject(
                                    name = "Social organization sign-up",
                                    value = """
                                            {
                                              "idToken": "eyJhbGciOiJSUzI1NiIsImtpZCI6IjEifQ...",
                                              "authorizationCode": null,
                                              "redirectUri": "https://coldtrace-frontend-liard.vercel.app/identity-access/sign-up",
                                              "nonce": "nonce-123",
                                              "organizationName": "ColdTrace Market",
                                              "fullName": "Jane Smith"
                                            }
                                            """
                            ))))
    @SecurityRequirements
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Organization sign-up completed and user authenticated",
                    content = @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AuthenticatedUserResource.class))),
            @ApiResponse(responseCode = "400", description = "Malformed request",
                    content = @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResource.class))),
            @ApiResponse(responseCode = "401", description = "Provider validation failed",
                    content = @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResource.class))),
            @ApiResponse(responseCode = "409", description = "Provider identity or organization already exists",
                    content = @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResource.class))),
            @ApiResponse(responseCode = "503", description = "Provider configuration is missing",
                    content = @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResource.class)))
    })
    @PostMapping("/social/{provider}/organization-sign-up")
    public ResponseEntity<?> socialOrganizationSignUp(
            @PathVariable String provider,
            @Valid @RequestBody SocialOrganizationSignUpResource resource
    ) {
        log.debug("POST /api/v1/authentication/social/{}/organization-sign-up", provider);
        var command = SocialOrganizationSignUpCommandFromResourceAssembler.toCommandFromResource(provider, resource);
        var result = socialOrganizationSignUpCommandService.handle(command);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                authenticated -> AuthenticatedUserResourceFromEntityAssembler.toResourceFromEntity(
                        authenticated.user(),
                        authenticated.token()
                ),
                HttpStatus.OK
        );
    }
}
