package com.foodchain.identity_context;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@ComponentScan(basePackages = {"com.foodchain.identity_context", "com.foodchain.shared_domain"})
public class IdentityContextApplication {
    public static void main(String[] args) {
        SpringApplication.run(IdentityContextApplication.class, args);
    }
}