package io.ten1010.coaster.auth.web.oauth.v2;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.authentication.OAuth2AuthenticationValidator;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeRequestAuthenticationException;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeRequestAuthenticationProvider;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeRequestAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.util.StringUtils;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

public class AllowAnyRedirectUris {

    public static OAuth2AuthorizationCodeRequestAuthenticationProvider codeRequestAuthenticationProvider(
            RegisteredClientRepository registeredClientRepository,
            OAuth2AuthorizationService authorizationService,
            OAuth2AuthorizationConsentService authorizationConsentService) {
        OAuth2AuthorizationCodeRequestAuthenticationProvider provider =
                new OAuth2AuthorizationCodeRequestAuthenticationProvider(registeredClientRepository, authorizationService, authorizationConsentService);
        provider.setAuthenticationValidatorResolver(authenticationValidatorResolver());

        return provider;
    }

    private static Function<String, OAuth2AuthenticationValidator> authenticationValidatorResolver() {
        Map<String, OAuth2AuthenticationValidator> authenticationValidators = new HashMap<>();
        authenticationValidators.put(OAuth2ParameterNames.REDIRECT_URI, context -> {
        });
        authenticationValidators.put(OAuth2ParameterNames.SCOPE, context -> {
            OAuth2AuthorizationCodeRequestAuthenticationToken authorizationCodeRequestAuthentication =
                    context.getAuthentication();
            RegisteredClient registeredClient = context.get(RegisteredClient.class);
            Objects.requireNonNull(registeredClient);

            Set<String> requestedScopes = authorizationCodeRequestAuthentication.getScopes();
            Set<String> allowedScopes = registeredClient.getScopes();
            if (!requestedScopes.isEmpty() && !allowedScopes.containsAll(requestedScopes)) {
                throwError(OAuth2ErrorCodes.INVALID_SCOPE,
                        OAuth2ParameterNames.SCOPE,
                        authorizationCodeRequestAuthentication,
                        registeredClient);
            }
        });

        return authenticationValidators::get;
    }

    private static void throwError(String errorCode, String parameterName,
                                   OAuth2AuthorizationCodeRequestAuthenticationToken authorizationCodeRequestAuthentication,
                                   RegisteredClient registeredClient) {
        throwError(errorCode, parameterName, authorizationCodeRequestAuthentication, registeredClient, null);
    }

    private static void throwError(String errorCode, String parameterName,
                                   OAuth2AuthorizationCodeRequestAuthenticationToken authorizationCodeRequestAuthentication,
                                   RegisteredClient registeredClient, @Nullable OAuth2AuthorizationRequest authorizationRequest) {
        throwError(errorCode, parameterName, "https://datatracker.ietf.org/doc/html/rfc6749#section-4.1.2.1",
                authorizationCodeRequestAuthentication, registeredClient, authorizationRequest);
    }

    private static void throwError(String errorCode, String parameterName, String errorUri,
                                   OAuth2AuthorizationCodeRequestAuthenticationToken authorizationCodeRequestAuthentication,
                                   RegisteredClient registeredClient, @Nullable OAuth2AuthorizationRequest authorizationRequest) {

        boolean redirectOnError = true;
        if (errorCode.equals(OAuth2ErrorCodes.INVALID_REQUEST) &&
                (parameterName.equals(OAuth2ParameterNames.CLIENT_ID) ||
                        parameterName.equals(OAuth2ParameterNames.REDIRECT_URI) ||
                        parameterName.equals(OAuth2ParameterNames.STATE))) {
            redirectOnError = false;
        }

        OAuth2AuthorizationCodeRequestAuthenticationToken authorizationCodeRequestAuthenticationResult = authorizationCodeRequestAuthentication;

        if (redirectOnError && !StringUtils.hasText(authorizationCodeRequestAuthentication.getRedirectUri())) {
            String redirectUri = resolveRedirectUri(authorizationRequest, registeredClient);
            String state = authorizationCodeRequestAuthentication.isConsent() && authorizationRequest != null ?
                    authorizationRequest.getState() : authorizationCodeRequestAuthentication.getState();
            authorizationCodeRequestAuthenticationResult = from(authorizationCodeRequestAuthentication)
                    .redirectUri(redirectUri)
                    .state(state)
                    .build();
            authorizationCodeRequestAuthenticationResult.setAuthenticated(authorizationCodeRequestAuthentication.isAuthenticated());
        } else if (!redirectOnError && StringUtils.hasText(authorizationCodeRequestAuthentication.getRedirectUri())) {
            authorizationCodeRequestAuthenticationResult = from(authorizationCodeRequestAuthentication)
                    .redirectUri(null)        // Prevent redirects
                    .build();
            authorizationCodeRequestAuthenticationResult.setAuthenticated(authorizationCodeRequestAuthentication.isAuthenticated());
        }

        OAuth2Error error = new OAuth2Error(errorCode, "OAuth 2.0 Parameter: " + parameterName, errorUri);
        throw new OAuth2AuthorizationCodeRequestAuthenticationException(error, authorizationCodeRequestAuthenticationResult);
    }

    @Nullable
    private static String resolveRedirectUri(@Nullable OAuth2AuthorizationRequest authorizationRequest, @Nullable RegisteredClient registeredClient) {
        if (authorizationRequest != null && StringUtils.hasText(authorizationRequest.getRedirectUri())) {
            return authorizationRequest.getRedirectUri();
        }
        if (registeredClient != null) {
            return registeredClient.getRedirectUris().iterator().next();
        }
        return null;
    }

    private static OAuth2AuthorizationCodeRequestAuthenticationToken.Builder from(OAuth2AuthorizationCodeRequestAuthenticationToken authorizationCodeRequestAuthentication) {
        return OAuth2AuthorizationCodeRequestAuthenticationToken.with(authorizationCodeRequestAuthentication.getClientId(), (Authentication) authorizationCodeRequestAuthentication.getPrincipal())
                .authorizationUri(authorizationCodeRequestAuthentication.getAuthorizationUri())
                .redirectUri(authorizationCodeRequestAuthentication.getRedirectUri())
                .scopes(authorizationCodeRequestAuthentication.getScopes())
                .state(authorizationCodeRequestAuthentication.getState())
                .additionalParameters(authorizationCodeRequestAuthentication.getAdditionalParameters())
                .authorizationCode(authorizationCodeRequestAuthentication.getAuthorizationCode());
    }

}
