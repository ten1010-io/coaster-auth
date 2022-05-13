package io.ten1010.coaster.auth.web.core.v1;


import io.ten1010.coaster.auth.dao.UserRepository;
import io.ten1010.coaster.auth.domain.User;
import io.ten1010.coaster.auth.domain.UserDto;
import io.ten1010.coaster.auth.domain.UserService;
import io.ten1010.coaster.auth.web.ApiResourceList;
import io.ten1010.coaster.auth.web.ListOptions;
import io.ten1010.coaster.auth.web.UriConstants;
import io.ten1010.coaster.auth.web.exception.ResourceNotFoundException;
import io.ten1010.coaster.auth.web.jsonpatch.JsonPatch;
import io.ten1010.coaster.auth.web.meta.v1.Metadata;
import io.ten1010.coaster.auth.web.patcher.JsonPatchHandler;
import io.ten1010.coaster.auth.web.updater.Updater;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(UriConstants.APIS + UriConstants.API_CORE_V1 + CoreV1UriConstants.RES_USERS)
public class UserController {

    public static final String CONCEALED = "--- CONCEALED ---";

    private static UserApiResource toApiResource(User user) {
        Metadata.MetadataBuilder metaBuilder = Metadata.builder();
        user.getId().ifPresent(metaBuilder::id);
        user.getVersion().ifPresent(metaBuilder::version);
        user.getCreationTimestamp()
                .map(e -> ZonedDateTime.ofInstant(e, ZoneId.systemDefault()))
                .ifPresent(metaBuilder::creationTimestamp);

        UserApiResource.UserApiResourceBuilder resBuilder = UserApiResource.builder();
        resBuilder.metadata(metaBuilder.build());
        resBuilder.userId(user.getUserId());
        user.getKoreanName().ifPresent(resBuilder::koreanName);
        user.getPhoneNumber().ifPresent(resBuilder::phoneNumber);
        user.getEmail().ifPresent(resBuilder::email);
        user.getDepartment().ifPresent(resBuilder::department);
        resBuilder.password(CONCEALED);

        return resBuilder.build();
    }

    private static ApiResourceList<UserApiResource> toApiResources(ListOptions options, List<User> users) {
        List<UserApiResource> resources = users.stream().map(UserController::toApiResource).collect(Collectors.toList());
        ApiResourceList<UserApiResource> list = new ApiResourceList<>();
        list.setItems(resources);
        options.getLimit().ifPresent(list::setLimit);
        options.getPage().ifPresent(list::setPage);

        return list;
    }

    public static class FindByConstants {

        public static final String USER_ID = "userId";
        public static final String DEPARTMENT = "department";
        public static final String KOREAN_NAME = "koreanName";
        public static final String PHONE_NUMBER = "phoneNumber";
        public static final String EMAIL = "email";

    }

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private Updater<User, UserApiResource> updater;
    @Autowired
    private JsonPatchHandler<User> patchHandler;

    @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserApiResource> create(@RequestBody UserApiResource resource) {
        UserDto dto = UserDto.builder()
                .userId(resource.getUserId())
                .password(resource.getPassword())
                .koreanName(resource.getKoreanName())
                .phoneNumber(resource.getPhoneNumber())
                .email(resource.getEmail())
                .department(resource.getDepartment())
                .build();
        User created = this.userService.createUser(dto);

        return ResponseEntity.ok(toApiResource(created));
    }

    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResourceList<UserApiResource>> list(ListOptions options) {
        List<User> users = listUsers(options);

        return ResponseEntity.ok(toApiResources(options, users));
    }

    @GetMapping(value = "/{" + UriConstants.PATH_VAR_ID + "}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserApiResource> get(@PathVariable(UriConstants.PATH_VAR_ID) long id) {
        Optional<User> opt = this.userRepository.findById(id);
        if (opt.isEmpty()) {
            throw new ResourceNotFoundException();
        }

        return ResponseEntity.ok(toApiResource(opt.get()));
    }

    @PutMapping(value = "/{" + UriConstants.PATH_VAR_ID + "}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserApiResource> update(@PathVariable(UriConstants.PATH_VAR_ID) long id,
                                                  @RequestBody UserApiResource resource) {
        User updated = this.updater.update(id, resource);

        return ResponseEntity.ok(toApiResource(updated));
    }

    @PatchMapping(value = "/{" + UriConstants.PATH_VAR_ID + "}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserApiResource> patch(@PathVariable(UriConstants.PATH_VAR_ID) long id,
                                                 @RequestBody JsonPatch jsonPatch) {
        User patched = this.patchHandler.handle(id, jsonPatch);

        return ResponseEntity.ok(toApiResource(patched));
    }

    @DeleteMapping(value = "/{" + UriConstants.PATH_VAR_ID + "}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserApiResource> delete(@PathVariable(UriConstants.PATH_VAR_ID) long id) {
        Optional<User> opt = this.userRepository.findById(id);
        if (opt.isEmpty()) {
            throw new ResourceNotFoundException();
        }
        this.userService.deleteUser(opt.get());

        return ResponseEntity.ok(toApiResource(opt.get()));
    }

    @DeleteMapping("")
    public ResponseEntity<Void> deleteCollection(ListOptions options) {
        List<User> users = listUsers(options);
        this.userService.deleteUsers(users);

        return ResponseEntity.ok(null);
    }

    private List<User> listUsers(ListOptions options) {
        if (options.getQuery().isPresent()) {
            if (options.getFindBy().isEmpty()) {
                throw new IllegalArgumentException();
            }
            switch (options.getFindBy().get()) {
                case FindByConstants.USER_ID:
                    return this.userRepository.findAllByUserId(options.getQuery().get(), options.getPageable()).getContent();
                case FindByConstants.KOREAN_NAME:
                    return this.userRepository.findAllByKoreanName(options.getQuery().get(), options.getPageable()).getContent();
                case FindByConstants.PHONE_NUMBER:
                    return this.userRepository.findAllByPhoneNumber(options.getQuery().get(), options.getPageable()).getContent();
                case FindByConstants.EMAIL:
                    return this.userRepository.findAllByEmail(options.getQuery().get(), options.getPageable()).getContent();
                case FindByConstants.DEPARTMENT:
                    return this.userRepository.findAllByDepartment(options.getQuery().get(), options.getPageable()).getContent();
            }
        } else {
            return this.userRepository.findAll(options.getPageable()).getContent();
        }

        return new ArrayList<>();
    }

}
