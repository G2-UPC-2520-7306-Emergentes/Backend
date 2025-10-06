// interfaces/rest/AuthenticationController.java
package com.foodchain.identity_context.interfaces.rest;

import com.foodchain.identity_context.application.outbound.tokens.JwtTokenServiceImpl;
import com.foodchain.identity_context.domain.model.queries.GetUserByEmailQuery;
import com.foodchain.identity_context.domain.services.UserCommandService;
import com.foodchain.identity_context.domain.services.UserQueryService;
import com.foodchain.identity_context.interfaces.rest.resources.*;
import com.foodchain.identity_context.interfaces.rest.transform.SignInCommandFromResourceAssembler;
import com.foodchain.identity_context.interfaces.rest.transform.SignUpCommandFromResourceAssembler;
import com.foodchain.identity_context.interfaces.rest.transform.UserDetailsResourceFromEntityAssembler;
import com.foodchain.identity_context.interfaces.rest.transform.UserProfileResourceFromEntityAssembler;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/iam/auth")
public class AuthenticationController {
    private final UserCommandService userCommandService;
    private final UserQueryService userQueryService;
    private final JwtTokenServiceImpl tokenService;

    public AuthenticationController(UserCommandService userCommandService, UserQueryService userQueryService, JwtTokenServiceImpl tokenService) { // ACTUALIZA CONSTRUCTOR
        this.userCommandService = userCommandService;
        this.userQueryService = userQueryService;
        this.tokenService = tokenService;
    }

    @PostMapping("/sign-up")
    public ResponseEntity<UUID> signUp(@Valid @RequestBody SignUpResource resource) {
        var command = SignUpCommandFromResourceAssembler.toCommandFromResource(resource);
        var userId = userCommandService.handle(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(userId);
    }

    @PostMapping("/sign-in")
    public ResponseEntity<AuthenticatedUserResource> signIn(@Valid @RequestBody SignInResource resource) {
        var command = SignInCommandFromResourceAssembler.toCommandFromResource(resource);
        var token = userCommandService.handle(command);
        var authenticatedUserResource = new AuthenticatedUserResource(resource.email(), token);
        return ResponseEntity.ok(authenticatedUserResource);
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserProfileResource> getCurrentUser() {
        // Obtenemos el email del usuario autenticado a través del contexto de seguridad
        var email = SecurityContextHolder.getContext().getAuthentication().getName();

        var query = new GetUserByEmailQuery(email);
        var user = userQueryService.handle(query);

        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        var resource = UserProfileResourceFromEntityAssembler.toResourceFromEntity(user.get());
        return ResponseEntity.ok(resource);
    }

    @PostMapping("/validate")
    public ResponseEntity<UserDetailsResource> validateTokenForService(@RequestBody String token) {
        if (!tokenService.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        var email = tokenService.getEmailFromToken(token);
        var user = userQueryService.handle(new GetUserByEmailQuery(email));

        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var resource = UserDetailsResourceFromEntityAssembler.toResourceFromEntity(user.get());
        return ResponseEntity.ok(resource);
    }
}