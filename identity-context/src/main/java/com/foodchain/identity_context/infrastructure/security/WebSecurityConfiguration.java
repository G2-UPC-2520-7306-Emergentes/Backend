// infrastructure/security/WebSecurityConfiguration.java
package com.foodchain.identity_context.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import java.util.List;

@Configuration
@EnableMethodSecurity
public class WebSecurityConfiguration {

    private final BearerAuthorizationRequestFilter bearerAuthorizationRequestFilter;

    public WebSecurityConfiguration(BearerAuthorizationRequestFilter bearerAuthorizationRequestFilter) {
        this.bearerAuthorizationRequestFilter = bearerAuthorizationRequestFilter;
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(configurer -> configurer.configurationSource(request -> {
                    var cors = new CorsConfiguration();
                    cors.setAllowedOrigins(List.of("*"));
                    cors.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    cors.setAllowedHeaders(List.of("*"));
                    return cors;
                }))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(customizer -> customizer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/v1/iam/auth/sign-up", "/api/v1/iam/auth/sign-in", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .anyRequest().authenticated() // ¡AHORA SÍ! Todas las demás peticiones requieren autenticación.
                );

        // Añadimos nuestro filtro personalizado ANTES del filtro de autenticación estándar.
        http.addFilterBefore(bearerAuthorizationRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}