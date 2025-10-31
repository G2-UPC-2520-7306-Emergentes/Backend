// infrastructure/security/WebSecurityConfiguration.java
package com.foodchain.batch_management_context.infrastructure.security;

import com.foodchain.shared_domain.domain.services.IamService;
import com.foodchain.shared_infrastructure.infrastructure.security.BearerAuthorizationRequestFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
@EnableMethodSecurity
@EnableWebSecurity // So the HttpSecurity http bean can work... for some reason
public class WebSecurityConfiguration {

    @Bean
    public BearerAuthorizationRequestFilter authorizationRequestFilter(IamService iamService) {
        return new BearerAuthorizationRequestFilter(iamService);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, IamService iamService) throws Exception {
        http.cors(configurer -> configurer.configurationSource(request -> {
                    var cors = new CorsConfiguration();
                    cors.setAllowedOrigins(List.of("*"));
                    cors.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    cors.setAllowedHeaders(List.of("*"));
                    return cors;
                }))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        // Permitimos el acceso a Swagger UI y la documentación OpenAPI
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll()                        // TODAS las demás peticiones deben ser autenticadas.
                        .anyRequest().authenticated()
                );

        http.addFilterBefore(authorizationRequestFilter(iamService), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}