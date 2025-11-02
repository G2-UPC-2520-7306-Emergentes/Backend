// interfaces/rest/UsersController.java
package com.foodchain.identity_context.interfaces.rest;

import com.foodchain.identity_context.domain.model.commands.AssignUserRoleCommand;
import com.foodchain.identity_context.domain.model.queries.GetUserByEmailQuery;
import com.foodchain.identity_context.domain.services.UserCommandService;
import com.foodchain.identity_context.domain.services.UserQueryService;
import com.foodchain.identity_context.interfaces.rest.resources.AssignRoleResource;
import com.foodchain.identity_context.interfaces.rest.resources.UserResource;
import com.foodchain.identity_context.interfaces.rest.transform.UserResourceFromEntityAssembler;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequestMapping("/api/v1/iam/users")
public class UsersController {

    private final UserQueryService userQueryService;
    private final UserCommandService userCommandService;

    public UsersController(UserQueryService userQueryService, UserCommandService userCommandService) {
        this.userQueryService = userQueryService;
        this.userCommandService = userCommandService;
    }

    /**
     * Endpoint para obtener el perfil del usuario actualmente autenticado.
     */
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResource> getMyProfile() {
        // 1. Obtenemos el email del usuario desde el contexto de seguridad que nuestro filtro estableció.
        var userEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        // 2. Creamos una consulta para buscar al usuario.
        var query = new GetUserByEmailQuery(userEmail);

        // 3. Usamos el servicio de consulta para obtener la entidad de dominio.
        var user = userQueryService.handle(query)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        // 4. Usamos el assembler para convertir la entidad a un DTO de recurso.
        var userResource = UserResourceFromEntityAssembler.toResourceFromEntity(user);

        // 5. Devolvemos el recurso con un código 200 OK.
        return ResponseEntity.ok(userResource);
    }

    /**
     * Endpoint para que un administrador asigne un nuevo rol a un usuario.
     */
    @PutMapping("/{userId}/role")
    @PreAuthorize("hasRole('ENTERPRISE_ADMIN')")
    public ResponseEntity<Void> assignRoleToUser(@PathVariable UUID userId,
                                                 @Valid @RequestBody AssignRoleResource resource) {
        var command = new AssignUserRoleCommand(userId, resource.roleName());
        userCommandService.handle(command);
        return ResponseEntity.ok().build();
    }
}