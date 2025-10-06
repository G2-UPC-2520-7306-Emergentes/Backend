package com.foodchain.batch_management_context;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@Configuration // So BearerAuthorizationRequestFilter can act as a component
public class BatchManagementContextApplication {
    public static void main(String[] args) {
        SpringApplication.run(BatchManagementContextApplication.class, args);
    }
}