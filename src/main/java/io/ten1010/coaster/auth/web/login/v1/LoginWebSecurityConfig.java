package io.ten1010.coaster.auth.web.login.v1;

import io.ten1010.coaster.auth.web.UriConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class LoginWebSecurityConfig {

    private static final String API_URL = UriConstants.APIS + UriConstants.API_LOGIN_V1;
    private static final String LOGIN_URL = API_URL + LoginUriConstants.RES_LOGIN;
    private static final String LOGIN_FAIL_URL = API_URL + LoginUriConstants.RES_LOGIN + "?error";
    private static final String LOGOUT_URL = API_URL + LoginUriConstants.RES_LOGOUT;
    private static final String LOGOUT_SUCC_URL = API_URL + LoginUriConstants.RES_LOGOUT + "?logout";

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain loginSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                .antMatcher(API_URL + "/**")
                .csrf().disable()
                .formLogin(configurer -> configurer
                        .loginPage(LOGIN_URL)
                        .loginProcessingUrl(LOGIN_URL)
                        .failureUrl(LOGIN_FAIL_URL))
                .logout(configurer -> configurer
                        .logoutUrl(LOGOUT_URL)
                        .logoutSuccessUrl(LOGOUT_SUCC_URL))
                .build();
    }

}
