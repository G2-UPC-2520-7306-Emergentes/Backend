// EN: traceability-context/interfaces/rest/TraceabilityQueryController.java
package com.foodchain.traceability_context.interfaces.rest;

import com.foodchain.shared_domain.domain.model.aggregates.UserDetails;
import com.foodchain.traceability_context.application.outbound.iam.UserQueryService;
import com.foodchain.traceability_context.domain.model.entities.TraceabilityEvent;
import com.foodchain.traceability_context.domain.model.queries.GetPublicTraceabilityEventsByBatchIdQuery;
import com.foodchain.traceability_context.domain.model.queries.GetTraceabilityEventsByBatchIdQuery;
import com.foodchain.traceability_context.domain.services.TraceabilityQueryService;
import com.foodchain.traceability_context.interfaces.rest.resources.TraceabilityEventResource;
import com.foodchain.traceability_context.interfaces.rest.transform.TraceabilityEventResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/trace/history") // Usamos una nueva ruta base para las consultas
@Tag(name = "Traceability Queries", description = "API para la consulta pública y privada de historiales de trazabilidad.")
public class TraceabilityQueryController {

    private final TraceabilityQueryService traceabilityQueryService;
    private final UserQueryService userQueryService;

    public TraceabilityQueryController(TraceabilityQueryService traceabilityQueryService, UserQueryService userQueryService) {
        this.traceabilityQueryService = traceabilityQueryService;
        this.userQueryService = userQueryService;
    }

    /**
     * Endpoint PRIVADO para que los actores de la cadena de suministro vean el historial.
     * Requiere autenticación y verifica la propiedad del lote.
     */
    @GetMapping("/batch/{batchId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<TraceabilityEventResource>> getEventsForActor(
            @Parameter(description = "ID del lote a consultar") @PathVariable UUID batchId,
            @AuthenticationPrincipal UserDetails userDetails) {

        var query = new GetTraceabilityEventsByBatchIdQuery(batchId, userDetails.enterpriseId());
        var events = traceabilityQueryService.handle(query);
        var resources = events.stream()
                .map(TraceabilityEventResourceFromEntityAssembler::toResourceFromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(resources);
    }

    /**
     * Endpoint PÚBLICO para que los consumidores finales vean el historial.
     * NO requiere autenticación, pero SÍ valida que el lote esté en un estado público.
     */
    @Operation(summary = "Obtener el historial público de un lote", description = "Devuelve la lista de eventos para un lote específico. Solo funciona si el lote está en un estado público (ej. 'FOR_SALE' o 'CLOSED').")
    @GetMapping("/public/batch/{batchId}")
    public ResponseEntity<List<TraceabilityEventResource>> getPublicEvents(
            @Parameter(description = "ID del lote escaneado en el código QR") @PathVariable UUID batchId) {

        // La lógica de negocio para verificar el estado del lote irá en el QueryService
        var query = new GetPublicTraceabilityEventsByBatchIdQuery(batchId);
        var events = traceabilityQueryService.handle(query);
        var actorIds = events.stream().map(TraceabilityEvent::getActorId).distinct().toList();
        var actorNames = userQueryService.getUsernamesForIds(actorIds); // El controlador orquesta

        var resources = events.stream()
                .map(event -> TraceabilityEventResourceFromEntityAssembler.toResourceFromEntity(event, actorNames.get(event.getActorId())))
                .collect(Collectors.toList());
        return ResponseEntity.ok(resources);
    }
}