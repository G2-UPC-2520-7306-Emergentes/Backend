package com.foodchain.identity_context;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class IdentityContextApplication {
    public static void main(String[] args) {
        SpringApplication.run(IdentityContextApplication.class, args);
    }
}