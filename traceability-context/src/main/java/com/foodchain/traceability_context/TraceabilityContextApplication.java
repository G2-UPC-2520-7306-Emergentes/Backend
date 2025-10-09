package com.foodchain.traceability_context;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@Configuration // So BearerAuthorizationRequestFilter can act as a component
@EnableAutoConfiguration
@ComponentScan(basePackages = {"com.foodchain.traceability_context", "com.foodchain.shared_domain"})
public class TraceabilityContextApplication {

	public static void main(String[] args) {
		SpringApplication.run(TraceabilityContextApplication.class, args);
	}

}
