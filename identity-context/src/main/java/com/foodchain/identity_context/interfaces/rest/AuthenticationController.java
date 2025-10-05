// interfaces/rest/AuthenticationController.java
package com.foodchain.identity_context.interfaces.rest;

import com.foodchain.identity_context.domain.model.queries.GetUserByEmailQuery;
import com.foodchain.identity_context.domain.services.UserCommandService;
import com.foodchain.identity_context.domain.services.UserQueryService;
import com.foodchain.identity_context.interfaces.rest.resources.AuthenticatedUserResource;
import com.foodchain.identity_context.interfaces.rest.resources.SignInResource;
import com.foodchain.identity_context.interfaces.rest.resources.SignUpResource;
import com.foodchain.identity_context.interfaces.rest.resources.UserProfileResource;
import com.foodchain.identity_context.interfaces.rest.transform.SignInCommandFromResourceAssembler;
import com.foodchain.identity_context.interfaces.rest.transform.SignUpCommandFromResourceAssembler;
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

    public AuthenticationController(UserCommandService userCommandService, UserQueryService userQueryService) { // ACTUALIZA CONSTRUCTOR
        this.userCommandService = userCommandService;
        this.userQueryService = userQueryService;
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
}