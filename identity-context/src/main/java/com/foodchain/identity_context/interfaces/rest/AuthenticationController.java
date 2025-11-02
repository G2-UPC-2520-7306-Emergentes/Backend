// interfaces/rest/AuthenticationController.java
package com.foodchain.identity_context.interfaces.rest;

import com.foodchain.identity_context.application.outbound.tokens.JwtTokenServiceImpl;
import com.foodchain.identity_context.domain.model.commands.RequestPasswordResetCommand;
import com.foodchain.identity_context.domain.model.commands.ResetPasswordCommand;
import com.foodchain.identity_context.domain.model.queries.GetUserByEmailQuery;
import com.foodchain.identity_context.domain.services.UserCommandService;
import com.foodchain.identity_context.domain.services.UserQueryService;
import com.foodchain.identity_context.interfaces.rest.resources.*;
import com.foodchain.identity_context.interfaces.rest.transform.SignInCommandFromResourceAssembler;
import com.foodchain.identity_context.interfaces.rest.transform.SignUpCommandFromResourceAssembler;
import com.foodchain.identity_context.interfaces.rest.transform.UserDetailsResourceFromEntityAssembler;
import com.foodchain.identity_context.interfaces.rest.transform.UserProfileResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/iam/auth")
@Tag(name = "Authentication", description = "Endpoints para registro, autenticación y gestión de sesión de usuarios.")
public class AuthenticationController {

    private final UserCommandService userCommandService;
    private final UserQueryService userQueryService;
    private final JwtTokenServiceImpl tokenService;

    public AuthenticationController(UserCommandService userCommandService, UserQueryService userQueryService, JwtTokenServiceImpl tokenService) { // ACTUALIZA CONSTRUCTOR
        this.userCommandService = userCommandService;
        this.userQueryService = userQueryService;
        this.tokenService = tokenService;
    }

    @Operation(summary = "Registrar un nuevo usuario", description = "Crea una nueva cuenta de usuario en el sistema. Por defecto, se le asigna el rol 'ENTERPRISE_USER'.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos (ej. email ya existe o contraseña no cumple los requisitos)")
    })
    @PostMapping("/sign-up")
    public ResponseEntity<UUID> signUp(@Valid @RequestBody SignUpResource resource) {
        var command = SignUpCommandFromResourceAssembler.toCommandFromResource(resource);
        var userId = userCommandService.handle(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(userId);
    }

    @Operation(summary = "Iniciar sesión", description = "Autentica a un usuario con su email y contraseña y devuelve un token JWT si las credenciales son válidas.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Autenticación exitosa, devuelve el token JWT"),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas")
    })
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

    @Operation(summary = "Cerrar sesión", description = "Invalida el token JWT actual del usuario, añadiéndolo a una lista negra para que no pueda ser reutilizado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cierre de sesión exitoso"),
            @ApiResponse(responseCode = "400", description = "No se encontró un token de autorización en la petición")
    })
    @PostMapping("/sign-out")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> signOut(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            tokenService.invalidateToken(token);
            return ResponseEntity.ok().body("Cierre de sesión exitoso.");
        }
        return ResponseEntity.badRequest().body("No se encontró token de autorización.");
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> requestPasswordReset(@Valid @RequestBody ForgotPasswordResource resource) {
        userCommandService.handle(new RequestPasswordResetCommand(resource.email()));
        return ResponseEntity.ok("Si el email existe, se ha enviado un enlace de recuperación.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordResource resource) {
        userCommandService.handle(new ResetPasswordCommand(resource.token(), resource.newPassword()));
        return ResponseEntity.ok("Contraseña restablecida con éxito.");
    }
}