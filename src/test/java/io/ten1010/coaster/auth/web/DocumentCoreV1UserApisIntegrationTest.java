package io.ten1010.coaster.auth.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.TextNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.fge.jackson.jsonpointer.JsonPointer;
import com.github.fge.jackson.jsonpointer.JsonPointerException;
import io.ten1010.coaster.auth.web.core.v1.CoreV1UriConstants;
import io.ten1010.coaster.auth.web.core.v1.UserApiResource;
import io.ten1010.coaster.auth.web.jsonpatch.JsonPatch;
import io.ten1010.coaster.auth.web.jsonpatch.JsonPatchOperation;
import io.ten1010.coaster.auth.web.jsonpatch.ReplaceOperation;
import io.ten1010.coaster.auth.web.meta.v1.Metadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.reactive.function.client.ExchangeStrategies;

import java.util.List;

@SpringBootTest
@ExtendWith(RestDocumentationExtension.class)
@ActiveProfiles({"test-db", "in-memory-rsa-key-source", "local-oauth2", "allow-any-redirect-uris", "dummy-client-secret"})
@DirtiesContext
class DocumentCoreV1UserApisIntegrationTest {

    static void createUser(WebTestClient webTestClient,
                           String userId,
                           String password,
                           String koreanName,
                           String phoneNumber,
                           String email,
                           String department) {
        ObjectMapper mapper = new ObjectMapper();
        UserApiResource apiRes = UserApiResource.builder()
                .userId(userId)
                .password(password)
                .koreanName(koreanName)
                .phoneNumber(phoneNumber)
                .email(email)
                .department(department)
                .build();
        String body;
        try {
            body = mapper.writeValueAsString(apiRes);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        webTestClient
                .post()
                .uri(UriConstants.APIS + UriConstants.API_CORE_V1 + CoreV1UriConstants.RES_USERS)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .returnResult(Object.class)
                .getResponseBody()
                .blockFirst();
    }

    static UserApiResource findByUserId(WebTestClient webTestClient, String userId) {
        ApiResourceList<UserApiResource> list = webTestClient
                .get()
                .uri(builder -> builder
                        .path(UriConstants.APIS + UriConstants.API_CORE_V1 + CoreV1UriConstants.RES_USERS)
                        .queryParam("findBy", "userId")
                        .queryParam("query", userId)
                        .build())
                .exchange()
                .returnResult(new ParameterizedTypeReference<ApiResourceList<UserApiResource>>() {
                })
                .getResponseBody()
                .blockFirst();
        return list.getItems().get(0);
    }

    static void deleteUsers(WebTestClient webTestClient) {
        webTestClient
                .delete()
                .uri(builder -> builder
                        .path(UriConstants.APIS + UriConstants.API_CORE_V1 + CoreV1UriConstants.RES_USERS)
                        .build())
                .exchange();
    }

    @Autowired
    ServerProperties serverProperties;
    WebTestClient webTestClient;
    ObjectMapper mapper;

    DocumentCoreV1UserApisIntegrationTest() {
        this.mapper = new ObjectMapper();
    }

    @BeforeEach
    void setUp(WebApplicationContext context, RestDocumentationContextProvider restDocumentation) {
        ExchangeStrategies strategies = ExchangeStrategies
                .builder()
                .codecs(clientDefaultCodecsConfigurer -> {
                    ObjectMapper mapper = JsonMapper
                            .builder()
                            .addModule(new JavaTimeModule())
                            .build();
                    clientDefaultCodecsConfigurer.defaultCodecs().jackson2JsonDecoder(new Jackson2JsonDecoder(mapper, MediaType.APPLICATION_JSON));
                })
                .build();
        this.webTestClient = MockMvcWebTestClient
                .bindToApplicationContext(context)
                .configureClient()
                .exchangeStrategies(strategies)
                .filter(WebTestClientRestDocumentation.documentationConfiguration(restDocumentation)
                        .operationPreprocessors()
                        .withRequestDefaults(Preprocessors.prettyPrint())
                        .withResponseDefaults(Preprocessors.prettyPrint()))
                .build();
    }

    @Test
    @WithMockUser
    void document_create_user() {
        UserApiResource apiRes = UserApiResource.builder()
                .userId("dummyid")
                .password("dummypassword1@")
                .koreanName("더미이름")
                .phoneNumber("000-0000-0000")
                .email("dummy-user@dummy-host")
                .department("dummydept")
                .build();
        String body;
        try {
            body = this.mapper.writeValueAsString(apiRes);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        this.webTestClient
                .post()
                .uri(UriConstants.APIS + UriConstants.API_CORE_V1 + CoreV1UriConstants.RES_USERS)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectBody()
                .consumeWith(WebTestClientRestDocumentation.document("core/v1/user/create-user"));
        deleteUsers(this.webTestClient);
    }

    @Test
    @WithMockUser
    void document_create_user_error() {
        UserApiResource apiRes = UserApiResource.builder()
                .userId("dummy-id")
                .password("dummypassword1@")
                .koreanName("더미이름")
                .phoneNumber("000-0000-0000")
                .email("dummy-user@dummy-host")
                .department("dummydept")
                .build();
        String body;
        try {
            body = this.mapper.writeValueAsString(apiRes);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        this.webTestClient
                .post()
                .uri(UriConstants.APIS + UriConstants.API_CORE_V1 + CoreV1UriConstants.RES_USERS)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectBody()
                .consumeWith(WebTestClientRestDocumentation.document(
                        "core/v1/user/create-user-error",
                        Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint())));
        deleteUsers(this.webTestClient);
    }

    @Test
    @WithMockUser
    void document_list_users() {
        createUser(this.webTestClient, "userid1", "qwerty1234@", "이름", "000-0000-0000", "e1@host", "dept1");
        createUser(this.webTestClient, "userid2", "qwerty1234@", "이름", "000-0000-0000", "e2@host", "dept2");
        createUser(this.webTestClient, "userid3", "qwerty1234@", "이름", "000-0000-0000", "e3@host", "dept3");
        createUser(this.webTestClient, "userid4", "qwerty1234@", "이름", "000-0000-0000", "e4@host", "dept4");
        createUser(this.webTestClient, "userid5", "qwerty1234@", "이름", "000-0000-0000", "e5@host", "dept5");
        this.webTestClient
                .get()
                .uri(builder -> builder
                        .path(UriConstants.APIS + UriConstants.API_CORE_V1 + CoreV1UriConstants.RES_USERS)
                        .build())
                .exchange()
                .expectBody()
                .consumeWith(WebTestClientRestDocumentation.document("core/v1/user/list-users-all"));
        this.webTestClient
                .get()
                .uri(builder -> builder
                        .path(UriConstants.APIS + UriConstants.API_CORE_V1 + CoreV1UriConstants.RES_USERS)
                        .queryParam("limit", 2)
                        .queryParam("page", 1)
                        .build())
                .exchange()
                .expectBody()
                .consumeWith(WebTestClientRestDocumentation.document("core/v1/user/list-users-limit"));
        this.webTestClient
                .get()
                .uri(builder -> builder
                        .path(UriConstants.APIS + UriConstants.API_CORE_V1 + CoreV1UriConstants.RES_USERS)
                        .queryParam("findBy", "userId")
                        .queryParam("query", "userid2")
                        .build())
                .exchange()
                .expectBody()
                .consumeWith(WebTestClientRestDocumentation.document("core/v1/user/list-users-find-by"));
        deleteUsers(this.webTestClient);
    }

    @Test
    @WithMockUser
    void document_get_user() {
        this.webTestClient
                .get()
                .uri(UriConstants.APIS + UriConstants.API_CORE_V1 + CoreV1UriConstants.RES_USERS + "/1")
                .exchange()
                .expectBody()
                .consumeWith(WebTestClientRestDocumentation.document("core/v1/user/get-user"));
    }

    @Test
    @WithMockUser
    void document_update_user() {
        UserApiResource apiRes = UserApiResource.builder()
                .metadata(Metadata.builder().id(1L).version(0).build())
                .userId("userid1")
                .password("adminuser1@")
                .koreanName("새이름")
                .phoneNumber("111-1111-1111")
                .email("updated-user@updated-host")
                .department("updateddept")
                .build();
        String body;
        try {
            body = this.mapper.writeValueAsString(apiRes);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        createUser(this.webTestClient, "userid1", "qwerty1234@", "이름", "000-0000-0000", "e1@host", "dept1");
        UserApiResource found = findByUserId(this.webTestClient, "userid1");
        this.webTestClient
                .put()
                .uri(UriConstants.APIS + UriConstants.API_CORE_V1 + CoreV1UriConstants.RES_USERS + "/" + found.getMetadata().getId())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectBody()
                .consumeWith(WebTestClientRestDocumentation.document("core/v1/user/update-user"));
        deleteUsers(this.webTestClient);
    }

    @Test
    @WithMockUser
    void document_patch_user() {
        List<JsonPatchOperation> operations;
        try {
            operations = List.of(
                    new ReplaceOperation(new JsonPointer("/phoneNumber"), TextNode.valueOf("222-2222-2222")),
                    new ReplaceOperation(new JsonPointer("/department"), TextNode.valueOf("patcheddept")));
        } catch (JsonPointerException e) {
            throw new RuntimeException();
        }
        JsonPatch jsonPatch = new JsonPatch(operations);
        String body;
        try {
            body = this.mapper.writeValueAsString(jsonPatch);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        createUser(this.webTestClient, "userid1", "qwerty1234@", "이름", "000-0000-0000", "e1@host", "dept1");
        UserApiResource found = findByUserId(this.webTestClient, "userid1");
        this.webTestClient
                .patch()
                .uri(UriConstants.APIS + UriConstants.API_CORE_V1 + CoreV1UriConstants.RES_USERS + "/" + found.getMetadata().getId())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectBody()
                .consumeWith(WebTestClientRestDocumentation.document("core/v1/user/patch-user"));
        deleteUsers(this.webTestClient);
    }

    @Test
    @WithMockUser
    void document_delete_user() {
        createUser(this.webTestClient, "userid1", "qwerty1234@", "이름", "000-0000-0000", "e1@host", "dept1");
        UserApiResource found = findByUserId(this.webTestClient, "userid1");
        this.webTestClient
                .delete()
                .uri(UriConstants.APIS + UriConstants.API_CORE_V1 + CoreV1UriConstants.RES_USERS + "/" + found.getMetadata().getId())
                .exchange()
                .expectBody()
                .consumeWith(WebTestClientRestDocumentation.document("core/v1/user/delete-user"));
        deleteUsers(this.webTestClient);
    }

    @Test
    @WithMockUser
    void document_delete_users() {
        createUser(this.webTestClient, "userid1", "qwerty1234@", "이름", "000-0000-0000", "e1@host", "dept1");
        createUser(this.webTestClient, "userid2", "qwerty1234@", "이름", "000-0000-0000", "e2@host", "dept2");
        createUser(this.webTestClient, "userid3", "qwerty1234@", "이름", "000-0000-0000", "e3@host", "dept3");
        this.webTestClient
                .delete()
                .uri(builder -> builder
                        .path(UriConstants.APIS + UriConstants.API_CORE_V1 + CoreV1UriConstants.RES_USERS)
                        .queryParam("findBy", "department")
                        .queryParam("query", "dept2")
                        .build())
                .exchange()
                .expectBody()
                .consumeWith(WebTestClientRestDocumentation.document("core/v1/user/delete-users-find-by"));
        this.webTestClient
                .delete()
                .uri(builder -> builder
                        .path(UriConstants.APIS + UriConstants.API_CORE_V1 + CoreV1UriConstants.RES_USERS)
                        .build())
                .exchange()
                .expectBody()
                .consumeWith(WebTestClientRestDocumentation.document("core/v1/user/delete-users-all"));
    }

}
