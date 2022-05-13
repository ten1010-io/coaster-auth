package io.ten1010.coaster.auth.web.core.v1;

import io.ten1010.coaster.auth.dao.UserRepository;
import io.ten1010.coaster.auth.domain.PasswordEncoder;
import io.ten1010.coaster.auth.domain.User;
import io.ten1010.coaster.auth.web.patcher.JsonPatchHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class CoreV1Config {

    @Bean
    public JsonPatchHandler<User> patchHandler(UserRepository repository, PasswordEncoder passwordEncoder) {
        return new JsonPatchHandler<>(
                repository,
                List.of(PasswordPatchers.addPatcher(passwordEncoder),
                        PasswordPatchers.replacePatcher(passwordEncoder),
                        KoreanNamePatchers.addPatcher(),
                        KoreanNamePatchers.replacePatcher(),
                        PhoneNumberPatchers.addPatcher(),
                        PhoneNumberPatchers.replacePatcher(),
                        EmailPatchers.addPatcher(),
                        EmailPatchers.replacePatcher(),
                        DepartmentPatchers.addPatcher(),
                        DepartmentPatchers.replacePatcher()));
    }

    @Bean
    public UserUpdater updater(UserRepository repository, PasswordEncoder passwordEncoder) {
        return new UserUpdater(repository, passwordEncoder);
    }

}
