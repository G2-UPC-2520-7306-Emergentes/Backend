// batch-management-context/infrastructure/security/BearerAuthorizationRequestFilter.java
package com.foodchain.traceability_context.infrastructure.security;

import com.foodchain.traceability_context.application.outbound.iam.IamService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.stream.Collectors;

public class BearerAuthorizationRequestFilter extends OncePerRequestFilter {

    private final IamService iamService;

    public BearerAuthorizationRequestFilter(IamService iamService) {
        this.iamService = iamService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            // Usamos nuestro ACL (IamService) para validar el token
            iamService.validateTokenAndGetUserDetails(token).ifPresent(userDetails -> {
                // Si es válido, creamos la autenticación para Spring Security
                var authorities = userDetails.roles().stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

                var authentication = new UsernamePasswordAuthenticationToken(
                        userDetails.email(), null, authorities);

                SecurityContextHolder.getContext().setAuthentication(authentication);
            });
        }
        filterChain.doFilter(request, response);
    }
}