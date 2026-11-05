package com.acme.coldtrace.platform.iam.application.internal.commandservices;

import com.acme.coldtrace.platform.iam.application.commandservices.SocialIdentityProfileCommandResult;
import com.acme.coldtrace.platform.iam.application.commandservices.SocialIdentityProfileCommandService;
import com.acme.coldtrace.platform.iam.application.internal.outboundservices.social.ExternalIdentityProviderService;
import com.acme.coldtrace.platform.iam.application.internal.outboundservices.social.ProviderIdentity;
import com.acme.coldtrace.platform.iam.domain.model.commands.SocialSignInCommand;
import com.acme.coldtrace.platform.shared.application.result.ApplicationError;
import com.acme.coldtrace.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;

/**
 * Application service that validates a provider identity and returns onboarding profile hints.
 *
 * @since 1.0
 */
@Service
public class SocialIdentityProfileCommandServiceImpl implements SocialIdentityProfileCommandService {
    private final ExternalIdentityProviderService externalIdentityProviderService;

    public SocialIdentityProfileCommandServiceImpl(
            ExternalIdentityProviderService externalIdentityProviderService
    ) {
        this.externalIdentityProviderService = externalIdentityProviderService;
    }

    @Override
    public Result<SocialIdentityProfileCommandResult, ApplicationError> handle(SocialSignInCommand command) {
        var providerIdentityResult = externalIdentityProviderService.validate(command);
        if (providerIdentityResult.isFailure()) {
            return Result.failure(providerIdentityResult.failure().orElseThrow());
        }

        return Result.success(toResult(providerIdentityResult.success().orElseThrow()));
    }

    private SocialIdentityProfileCommandResult toResult(ProviderIdentity providerIdentity) {
        var email = normalize(providerIdentity.email());
        var fullName = firstNonBlank(
                providerIdentity.fullName(),
                joinName(providerIdentity.givenName(), providerIdentity.familyName()),
                suggestNameFromEmail(email)
        );
        return new SocialIdentityProfileCommandResult(providerIdentity.idToken(), email, fullName);
    }

    private String firstNonBlank(String... values) {
        for (var value : values) {
            if (value != null && !value.isBlank()) {
                return value.trim();
            }
        }
        return "";
    }

    private String joinName(String givenName, String familyName) {
        return "%s %s".formatted(normalize(givenName), normalize(familyName)).trim();
    }

    private String normalize(String value) {
        return value != null ? value.trim() : "";
    }

    private String suggestNameFromEmail(String email) {
        if (email.isBlank() || !email.contains("@")) {
            return "";
        }
        var localPart = email.substring(0, email.indexOf('@')).replaceAll("[._-]+", " ").trim();
        if (localPart.isBlank()) {
            return "";
        }
        var words = localPart.split("\\s+");
        var suggestedName = new StringBuilder();
        for (var word : words) {
            if (word.isBlank()) {
                continue;
            }
            if (suggestedName.length() > 0) {
                suggestedName.append(' ');
            }
            suggestedName
                    .append(Character.toUpperCase(word.charAt(0)))
                    .append(word.substring(1).toLowerCase());
        }
        return suggestedName.toString();
    }
}
