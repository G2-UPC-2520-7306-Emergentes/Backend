// interfaces/rest/StepController.java
package com.foodchain.traceability_context.interfaces.rest;

import com.foodchain.traceability_context.application.outbound.iam.IamService;
import com.foodchain.traceability_context.application.outbound.iam.UserDetails;
import com.foodchain.traceability_context.domain.services.TraceabilityCommandService;
import com.foodchain.traceability_context.interfaces.rest.resources.RegisterStepResource;
import com.foodchain.traceability_context.interfaces.rest.transform.RegisterTraceabilityEventCommandFromResourceAssembler;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/trace/events")
public class StepController {

    private final TraceabilityCommandService traceabilityCommandService;
    private final IamService iamService; // Inyectamos el ACL para obtener info del usuario

    public StepController(TraceabilityCommandService traceabilityCommandService, IamService iamService) {
        this.traceabilityCommandService = traceabilityCommandService;
        this.iamService = iamService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ENTERPRISE_USER')") // Solo usuarios con este rol pueden registrar eventos
    public ResponseEntity<UUID> registerStep(@Valid @RequestBody RegisterStepResource resource,
                                             @RequestHeader("Authorization") String authorizationHeader) {

        // 1. Extraer el token del header
        String token = authorizationHeader.substring(7); // Quita "Bearer "

        // 2. Usar el ACL para obtener el ID del usuario autenticado
        UUID actorId = iamService.validateTokenAndGetUserDetails(token)
                .map(UserDetails::userId) // Extraemos el userId del DTO
                .orElseThrow(() -> new IllegalArgumentException("Invalid or missing user information in token."));

        // 3. Crear el comando usando el Assembler, ahora con el actorId
        var command = RegisterTraceabilityEventCommandFromResourceAssembler.toCommandFromResource(resource, actorId);

        // 4. Delegar al servicio de aplicación
        var eventId = traceabilityCommandService.handle(command);

        return ResponseEntity.status(HttpStatus.CREATED).body(eventId);
    }
}