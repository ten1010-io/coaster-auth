package io.ten1010.coaster.auth.spring;

import io.ten1010.coaster.auth.dao.UserRepository;
import io.ten1010.coaster.auth.domain.PasswordEncoder;
import io.ten1010.coaster.auth.domain.SetupDataInitializer;
import io.ten1010.coaster.auth.domain.UserService;
import io.ten1010.coaster.auth.domain.UserServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainConfig {

    @Bean
    public UserService userService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return new UserServiceImpl(userRepository, passwordEncoder);
    }

    @Bean
    public PasswordEncoder domainPasswordEncoder(org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {
        return passwordEncoder::encode;
    }

    @Bean
    public SetupDataInitializer setupDataInitializer(UserService userService) {
        return new SetupDataInitializer(userService);
    }

}
