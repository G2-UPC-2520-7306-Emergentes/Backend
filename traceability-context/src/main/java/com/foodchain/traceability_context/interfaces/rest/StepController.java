// interfaces/rest/StepController.java
package com.foodchain.traceability_context.interfaces.rest;

import com.foodchain.shared_domain.domain.model.aggregates.UserDetails;
import com.foodchain.traceability_context.domain.services.TraceabilityCommandService;
import com.foodchain.traceability_context.interfaces.rest.resources.RegisterStepResource;
import com.foodchain.traceability_context.interfaces.rest.transform.RegisterTraceabilityEventCommandFromResourceAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/trace/events")
@Tag(name = "Traceability Events", description = "API para el registro de eventos en la cadena de suministro.")
public class StepController {

    private final TraceabilityCommandService traceabilityCommandService;

    public StepController(TraceabilityCommandService traceabilityCommandService) {
        this.traceabilityCommandService = traceabilityCommandService;
    }

    @Operation(
            summary = "Registrar un nuevo evento de trazabilidad",
            description = """
                    Registra un paso en el historial de un lote. El `actorId` se extrae del token. Las coordenadas GPS deben ser enviadas por el cliente.
                    
                    **Tipos de evento (`eventType`) soportados:**
                    * `COSECHA_INICIADA`: Inicio de la cosecha en el campo.
                    * `PROCESAMIENTO_INICIADO`: El lote llega a la planta de procesamiento.
                    * `PROCESAMIENTO_COMPLETADO`: El lote ha sido procesado.
                    * `EMPAQUE_INICIADO`: Comienza el empaque del producto final.
                    * `EMPAQUE_COMPLETADO`: El producto ha sido empacado.
                    * `DESPACHO_DESDE_PLANTA`: El lote sale de la planta hacia un distribuidor.
                    * `RECEPCION_EN_CENTRO_DISTRIBUCION`: El lote es recibido en el almacén de un distribuidor.
                    * `DISPONIBLE_PARA_VENTA`: El producto llega a la tienda final y está listo para el consumidor.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Evento registrado y publicado en la cola de mensajería exitosamente."),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos (ej. batchId nulo, coordenadas inválidas)."),
            @ApiResponse(responseCode = "403", description = "No autorizado.")
    })
    @PostMapping
    @PreAuthorize("hasRole('ENTERPRISE_USER') or hasRole('ENTERPRISE_ADMIN')")
    public ResponseEntity<UUID> registerStep(@Valid @RequestBody RegisterStepResource resource,
                                             @AuthenticationPrincipal UserDetails userDetails) {
        // 1. Crear el comando usando el Assembler, ahora con el actorId
        var command = RegisterTraceabilityEventCommandFromResourceAssembler.toCommandFromResource(resource, userDetails.userId());

        // 2. Delegar al servicio de aplicación
        var eventId = traceabilityCommandService.handle(command);

        return ResponseEntity.status(HttpStatus.CREATED).body(eventId);
    }
}