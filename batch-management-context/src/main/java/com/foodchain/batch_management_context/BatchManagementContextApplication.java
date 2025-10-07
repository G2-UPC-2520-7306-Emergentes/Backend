package com.foodchain.batch_management_context;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@Configuration // So BearerAuthorizationRequestFilter can act as a component
@EnableAutoConfiguration
@ComponentScan(basePackages = {"com.foodchain.batch_management_context", "com.foodchain.shared_domain"})
public class BatchManagementContextApplication {
    public static void main(String[] args) {
        SpringApplication.run(BatchManagementContextApplication.class, args);
    }
}