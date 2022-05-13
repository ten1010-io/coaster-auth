package io.ten1010.coaster.auth.web;

import io.ten1010.coaster.auth.web.core.v1.UserDomainModelPropertyPathAdapter;
import io.ten1010.coaster.auth.web.exceptionadapter.DomainModelPropertyExceptionAdapterManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class WebConfig {

    @Bean
    public DomainModelPropertyExceptionAdapterManager adapter() {
        return new DomainModelPropertyExceptionAdapterManager(List.of(new UserDomainModelPropertyPathAdapter()));
    }

}
