package io.ten1010.coaster.auth.domain;

import io.ten1010.coaster.auth.common.PropertyUserVisibleException;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;

@Slf4j
public class SetupDataInitializer {

    public static final String INITIAL_ADMIN_USER_PASSWORD = "adminuser1@";

    private UserService userService;

    public SetupDataInitializer(UserService userService) {
        this.userService = userService;
    }

    @PostConstruct
    public void init() {
        createRootUser();
    }

    private void createRootUser() {
        try {
            UserDto dto = UserDto.builder()
                    .userId(UserServiceImpl.ADMIN_USER_ID)
                    .password(INITIAL_ADMIN_USER_PASSWORD)
                    .build();
            this.userService.createUser(dto);
            log.info("User [\"adminuser\"] created");
        } catch (PropertyUserVisibleException e) {
            if (e.getMessage().equals(UserServiceConstants.MSG_USER_ID_ALREADY_EXIST)) {
                log.info("User [\"adminuser\"] not created because it already exists");
                return;
            }
            throw e;
        }
    }

}
