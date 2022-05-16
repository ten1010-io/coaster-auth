package io.ten1010.coaster.auth.web.oauth.v2;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import io.ten1010.coaster.auth.web.UriConstants;
import io.ten1010.coaster.auth.web.login.v1.LoginUriConstants;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.authorization.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.InMemoryOAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.InMemoryOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.ProviderSettings;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Configuration
public class Oauth2WebSecurityConfig {

    private static final String LOGIN_PAGE_URL = UriConstants.APIS + UriConstants.API_LOGIN_V1 + LoginUriConstants.RES_LOGIN;

    @Component
    @ConfigurationProperties(prefix = "app.oauth2")
    @Data
    public static class Oauth2Properties {

        private String issuer;
        private String clientId;
        private String clientSecret;
        private List<String> clientRedirectUris;

        public Oauth2Properties() {
            this.clientRedirectUris = new ArrayList<>();
        }

    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain oauthSecurityFilterChain(
            HttpSecurity http,
            Environment environment,
            RegisteredClientRepository registeredClientRepository,
            OAuth2AuthorizationService authorizationService,
            OAuth2AuthorizationConsentService authorizationConsentService) throws Exception {
        OAuth2AuthorizationServerConfigurer<HttpSecurity> authorizationServerConfigurer = new OAuth2AuthorizationServerConfigurer<>();
        if (Set.of(environment.getActiveProfiles()).contains("allow-any-redirect-uris")) {
            authorizationServerConfigurer.authorizationEndpoint(configurer -> configurer.authenticationProvider(
                    AllowAnyRedirectUris.codeRequestAuthenticationProvider(registeredClientRepository, authorizationService, authorizationConsentService)));
        }
        RequestMatcher endpointsMatcher = authorizationServerConfigurer.getEndpointsMatcher();

        http.requestMatcher(endpointsMatcher)
                .authorizeRequests(authorizeRequests -> authorizeRequests.anyRequest().authenticated())
                .formLogin(configurer -> configurer.loginPage(LOGIN_PAGE_URL))
                .csrf(csrf -> csrf.ignoringRequestMatchers(endpointsMatcher))
                .apply(authorizationServerConfigurer);

        return http.build();
    }

    @Bean
    public RegisteredClientRepository registeredClientRepository(Oauth2Properties properties, PasswordEncoder passwordEncoder) {
        RegisteredClient registeredClient = RegisteredClient
                .withId(properties.getClientId())
                .clientId(properties.getClientId())
                .clientSecret(passwordEncoder.encode(properties.getClientSecret()))
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .redirectUris(uris -> uris.addAll(properties.getClientRedirectUris()))
                .scope(OidcScopes.OPENID)
                .build();

        return new InMemoryRegisteredClientRepository(registeredClient);
    }

    @Bean
    public OAuth2AuthorizationService authorizationService() {
        return new InMemoryOAuth2AuthorizationService();
    }

    @Bean
    public OAuth2AuthorizationConsentService authorizationConsentService() {
        return new InMemoryOAuth2AuthorizationConsentService();
    }

    @Bean
    public ProviderSettings providerSettings(Oauth2Properties properties) {
        return ProviderSettings.builder()
                .issuer(properties.getIssuer())
                .authorizationEndpoint(UriConstants.APIS + UriConstants.API_OAUTH2 + Oauth2UriConstants.RES_AUTHORIZE)
                .tokenEndpoint(UriConstants.APIS + UriConstants.API_OAUTH2 + Oauth2UriConstants.RES_TOKEN)
                .jwkSetEndpoint(UriConstants.APIS + UriConstants.API_OAUTH2 + Oauth2UriConstants.RES_JWKS)
                .tokenRevocationEndpoint(UriConstants.APIS + UriConstants.API_OAUTH2 + Oauth2UriConstants.RES_REVOKE)
                .tokenIntrospectionEndpoint(UriConstants.APIS + UriConstants.API_OAUTH2 + Oauth2UriConstants.RES_INTROSPECT)
                .oidcClientRegistrationEndpoint(UriConstants.APIS + UriConstants.API_OAUTH2 + Oauth2UriConstants.RES_OIDC_REGISTER)
                .oidcUserInfoEndpoint(UriConstants.APIS + UriConstants.API_OAUTH2 + Oauth2UriConstants.RES_OIDC_USERINFO)
                .build();
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource(RSAKeySource rsaKeySource) {
        return (jwkSelector, securityContext) -> jwkSelector.select(new JWKSet(rsaKeySource.get()));
    }

}
