package io.ten1010.coaster.auth.web;

import io.ten1010.coaster.auth.web.UriConstants;
import io.ten1010.coaster.auth.web.login.v1.LoginUriConstants;
import io.ten1010.coaster.auth.web.oauth.v2.Oauth2UriConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;

import java.util.concurrent.atomic.AtomicReference;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ExtendWith(RestDocumentationExtension.class)
@ActiveProfiles({"test-db", "in-memory-rsa-key-source", "local-oauth2", "allow-any-redirect-uris", "dummy-client-secret"})
@DirtiesContext
class DocumentOauth2CodeGrantFlowExampleIntegrationTest {

    @Autowired
    ServerProperties serverProperties;
    WebTestClient webTestClient;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        this.webTestClient = WebTestClient.bindToServer().baseUrl("http://localhost:" + serverProperties.getPort())
                .filter(WebTestClientRestDocumentation.documentationConfiguration(restDocumentation)
                        .operationPreprocessors()
                        .withRequestDefaults(Preprocessors.prettyPrint())
                        .withResponseDefaults(Preprocessors.prettyPrint()))
                .build();
    }

    @Test
    void document_oauth2_code_grant_flow_delegating_to_client_example() {
        MultiValueMap<String, String> cookies = new LinkedMultiValueMap<>();
        AtomicReference<String> codeRef = new AtomicReference<>();
        this.webTestClient
                .post()
                .uri(UriConstants.APIS + UriConstants.API_LOGIN_V1 + LoginUriConstants.RES_LOGIN)
                .cookies(map -> map.addAll(cookies))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("username", "adminuser").with("password", "adminuser1@"))
                .exchange()
                .expectBody()
                .consumeWith(WebTestClientRestDocumentation.document("oauth2-code-grant-flow-example/delegating-to-client/1-post-login"))
                .consumeWith(result -> result.getResponseCookies().forEach((key, value) -> {
                    cookies.remove(key);
                    cookies.add(key, value.get(0).getValue());
                }));
        this.webTestClient
                .post()
                .uri(builder -> builder
                        .path(UriConstants.APIS + UriConstants.API_OAUTH2 + Oauth2UriConstants.RES_AUTHORIZE)
                        .queryParam("response_type", "code")
                        .queryParam("client_id", "openid-client")
                        .queryParam("scope", "openid")
                        .queryParam("redirect_uri", "http://dummy-host")
                        .build())
                .cookies(map -> map.addAll(cookies))
                .exchange()
                .expectBody()
                .consumeWith(WebTestClientRestDocumentation.document("oauth2-code-grant-flow-example/delegating-to-client/2-post-authorize"))
                .consumeWith(result -> {
                    String query = result.getResponseHeaders().getLocation().getQuery();
                    String code = query.split("=")[1];
                    codeRef.set(code);
                });
        this.webTestClient
                .post()
                .uri(UriConstants.APIS + UriConstants.API_OAUTH2 + Oauth2UriConstants.RES_TOKEN)
                .header("Authorization", "Basic b3BlbmlkLWNsaWVudDpkdW1teQ==")
                .body(BodyInserters
                        .fromFormData("grant_type", "authorization_code")
                        .with("code", codeRef.get())
                        .with("redirect_uri", "http://dummy-host"))
                .exchange()
                .expectBody()
                .consumeWith(WebTestClientRestDocumentation.document("oauth2-code-grant-flow-example/post-token"));
    }

    @Test
    void document_oauth2_code_grant_flow_redirection_login_page() {
        this.webTestClient
                .get()
                .uri(builder -> builder
                        .path(UriConstants.APIS + UriConstants.API_OAUTH2 + Oauth2UriConstants.RES_AUTHORIZE)
                        .queryParam("response_type", "code")
                        .queryParam("client_id", "openid-client")
                        .queryParam("scope", "openid")
                        .queryParam("redirect_uri", "http://dummy-host")
                        .build())
                .exchange()
                .expectBody()
                .consumeWith(WebTestClientRestDocumentation.document("oauth2-code-grant-flow-example/redirection-login-page/1-get-authorize"));
    }

}
