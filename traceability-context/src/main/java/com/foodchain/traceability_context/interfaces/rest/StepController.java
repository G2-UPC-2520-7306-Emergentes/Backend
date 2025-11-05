// interfaces/rest/StepController.java
package com.foodchain.traceability_context.interfaces.rest;

import com.foodchain.shared_domain.domain.model.aggregates.UserDetails;
import com.foodchain.traceability_context.domain.model.queries.GetTraceabilityEventsByBatchIdQuery;
import com.foodchain.traceability_context.domain.services.TraceabilityCommandService;
import com.foodchain.traceability_context.domain.services.TraceabilityQueryService;
import com.foodchain.traceability_context.interfaces.rest.resources.CorrectStepResource;
import com.foodchain.traceability_context.interfaces.rest.resources.RegisterStepResource;
import com.foodchain.traceability_context.interfaces.rest.resources.TraceabilityEventResource;
import com.foodchain.traceability_context.interfaces.rest.transform.CorrectTraceabilityEventCommandFromResourceAssembler;
import com.foodchain.traceability_context.interfaces.rest.transform.RegisterTraceabilityEventCommandFromResourceAssembler;
import com.foodchain.traceability_context.interfaces.rest.transform.TraceabilityEventResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/trace/events")
@Tag(name = "Traceability Events", description = "API para el registro de eventos en la cadena de suministro.")
public class StepController {

    private final TraceabilityCommandService traceabilityCommandService;
    private final TraceabilityQueryService traceabilityQueryService;

    public StepController(TraceabilityCommandService traceabilityCommandService, TraceabilityQueryService traceabilityQueryService) {
        this.traceabilityCommandService = traceabilityCommandService;
        this.traceabilityQueryService = traceabilityQueryService;
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
    @PostMapping(consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ENTERPRISE_USER') or hasRole('ENTERPRISE_ADMIN')")
    public ResponseEntity<TraceabilityEventResource> registerStep(
            @Valid @RequestPart("event") RegisterStepResource resource,
            @RequestPart(value = "file", required = false) MultipartFile file,
            @AuthenticationPrincipal UserDetails userDetails) {

        // 1. Crear el comando usando el Assembler, ahora con el actorId
        var command = RegisterTraceabilityEventCommandFromResourceAssembler.toCommandFromResource(resource, userDetails.userId(), file);

        // 2.  El servicio ahora devuelve la entidad completa
        var newEvent = traceabilityCommandService.handle(command);

        // 3. La convertimos al recurso de la API
        var eventResource = TraceabilityEventResourceFromEntityAssembler.toResourceFromEntity(newEvent);

        // 4. Devolvemos 201 Created con el recurso completo en el cuerpo
        return ResponseEntity.status(HttpStatus.CREATED).body(eventResource);
    }

    @Operation(summary = "Obtener el historial de un lote", description = "Devuelve la lista completa de eventos de trazabilidad para un lote específico, ordenados por fecha.")
    @ApiResponses()
    @GetMapping("/batch/{batchId}")
    @PreAuthorize("isAuthenticated()") // La autorización de propiedad se hace en la capa de aplicación
    public ResponseEntity<List<TraceabilityEventResource>> getEventsByBatch(
            @Parameter(description = "ID del lote a consultar") @PathVariable UUID batchId,
            @AuthenticationPrincipal UserDetails userDetails) {

        var query = new GetTraceabilityEventsByBatchIdQuery(batchId, userDetails.enterpriseId());
        var events = traceabilityQueryService.handle(query);
        var resources = events.stream()
                .map(TraceabilityEventResourceFromEntityAssembler::toResourceFromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(resources);
    }

    @PostMapping(path = "/{originalEventId}/correction", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ENTERPRISE_ADMIN')") // ¡Solo un admin puede corregir!
    public ResponseEntity<TraceabilityEventResource> correctStep(
            @Parameter(description = "ID del evento original a corregir") @PathVariable UUID originalEventId,
            @Valid @RequestPart("correction") CorrectStepResource resource,
            @RequestPart(value = "file", required = false) MultipartFile file,
            @AuthenticationPrincipal UserDetails userDetails) {

        var command = CorrectTraceabilityEventCommandFromResourceAssembler.toCommandFromResource(
                originalEventId, resource, userDetails.userId(), file);
        var correctedEvent = traceabilityCommandService.handle(command);

        // También usamos la versión simple aquí.
        var eventResource = TraceabilityEventResourceFromEntityAssembler.toResourceFromEntity(correctedEvent);

        return ResponseEntity.status(HttpStatus.CREATED).body(eventResource);
    }
}
