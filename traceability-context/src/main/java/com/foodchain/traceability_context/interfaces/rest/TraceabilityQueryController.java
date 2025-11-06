// EN: traceability-context/interfaces/rest/TraceabilityQueryController.java
package com.foodchain.traceability_context.interfaces.rest;

import com.foodchain.shared_domain.domain.model.aggregates.UserDetails;
import com.foodchain.traceability_context.application.outbound.iam.UserQueryService;
import com.foodchain.traceability_context.domain.model.entities.TraceabilityEvent;
import com.foodchain.traceability_context.domain.model.queries.GetPublicTraceabilityEventsByBatchIdQuery;
import com.foodchain.traceability_context.domain.model.queries.GetTraceabilityEventsByBatchIdQuery;
import com.foodchain.traceability_context.domain.services.TraceabilityQueryService;
import com.foodchain.traceability_context.interfaces.rest.resources.RoutePointResource;
import com.foodchain.traceability_context.interfaces.rest.resources.TraceabilityEventResource;
import com.foodchain.traceability_context.interfaces.rest.transform.RoutePointResourceFromEntityAssembler;
import com.foodchain.traceability_context.interfaces.rest.transform.TraceabilityEventResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/trace/history") // Usamos una nueva ruta base para las consultas
@Tag(name = "Traceability Queries", description = "API para la consulta pública y privada de historiales de trazabilidad.")
public class TraceabilityQueryController {

    private final TraceabilityQueryService traceabilityQueryService;
    private final UserQueryService userQueryService;
    private final String txUrlTemplate;

    public TraceabilityQueryController(TraceabilityQueryService tqs, UserQueryService uqs,
                                       @Value("${blockchain.explorer.tx-url-template}") String txUrlTemplate) {
        this.traceabilityQueryService = tqs;
        this.userQueryService = uqs;
        this.txUrlTemplate = txUrlTemplate;
    }
    /**
     * Endpoint PRIVADO para que los actores de la cadena de suministro vean el historial.
     * Requiere autenticación y verifica la propiedad del lote.
     */
    @GetMapping("/batch/{batchId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<TraceabilityEventResource>> getEventsForActor(
            @PathVariable UUID batchId,
            @AuthenticationPrincipal UserDetails userDetails,
            Pageable pageable) { // Spring MVC crea este objeto a partir de los parámetros ?page=X&size=Y&sort=Z

        var query = new GetTraceabilityEventsByBatchIdQuery(batchId, userDetails.enterpriseId());
        // El servicio ahora devuelve un Page de entidades
        var eventsPage = traceabilityQueryService.handle(query, pageable);

        // Usamos el método 'map' de Page para convertir su contenido sin perder la información de paginación
        var resourcesPage = eventsPage.map(event -> {
            // La orquestación para enriquecer con nombres se hace aquí
            String actorName = userQueryService.getUsernamesForIds(List.of(event.getActorId())).get(event.getActorId());
            return TraceabilityEventResourceFromEntityAssembler.toResourceFromEntity(event, actorName, txUrlTemplate);
        });
        return ResponseEntity.ok(resourcesPage);
    }


    /**
     * Endpoint PÚBLICO para que los consumidores finales vean el historial.
     * NO requiere autenticación, pero SÍ valida que el lote esté en un estado público.
     */
    @Operation(summary = "Obtener el historial público de un lote", description = "Devuelve la lista de eventos para un lote específico. Solo funciona si el lote está en un estado público (ej. 'FOR_SALE' o 'CLOSED').")
    @GetMapping("/public/batch/{batchId}")
    public ResponseEntity<Page<TraceabilityEventResource>> getPublicEvents(
            @PathVariable UUID batchId,
            Pageable pageable) {

        var query = new GetPublicTraceabilityEventsByBatchIdQuery(batchId);
        var eventsPage = traceabilityQueryService.handle(query, pageable);

        // Extraemos todos los IDs de la página actual para hacer una sola llamada de red
        var actorIds = eventsPage.getContent().stream().map(TraceabilityEvent::getActorId).distinct().toList();
        var actorNames = userQueryService.getUsernamesForIds(actorIds);

        var resourcesPage = eventsPage.map(event -> TraceabilityEventResourceFromEntityAssembler.toResourceFromEntity(
                event,
                actorNames.get(event.getActorId()),
                this.txUrlTemplate
        ));

        return ResponseEntity.ok(resourcesPage);
    }

    /**
     * Método privado de orquestación para enriquecer una lista de eventos con los nombres de los actores.
     */
    private List<TraceabilityEventResource> enrichEventsWithActorNames(List<TraceabilityEvent> events) {
        if (events == null || events.isEmpty()) {
            return Collections.emptyList();
        }

        // 1. Extraer los IDs únicos de los actores
        List<UUID> actorIds = events.stream()
                .map(TraceabilityEvent::getActorId)
                .distinct()
                .toList();

        // 2. Hacer UNA SOLA llamada de red para obtener todos los nombres
        Map<UUID, String> actorNames = userQueryService.getUsernamesForIds(actorIds);

        // 3. Mapear los eventos a recursos, usando la versión enriquecida del assembler
        return events.stream()
                .map(event -> TraceabilityEventResourceFromEntityAssembler.toResourceFromEntity(
                        event,
                        actorNames.get(event.getActorId()),
                        this.txUrlTemplate // ¡Pasamos el template!
                ))
                .collect(Collectors.toList());
    }

    /**
     * Endpoint PÚBLICO para obtener solo los puntos geográficos de la ruta de un lote.
     * Ideal para renderizar el mapa interactivo.
     */
    @Operation(summary = "Obtener la ruta geográfica de un lote", description = "Devuelve una lista ordenada de eventos que tienen coordenadas GPS, para dibujar el mapa del recorrido.")
    @GetMapping("/public/batch/{batchId}/route")
    public ResponseEntity<List<RoutePointResource>> getPublicRoute(
            @Parameter(description = "ID del lote escaneado") @PathVariable UUID batchId) {

        // 1. Usamos la misma lógica de negocio para obtener los eventos de un lote público
        var query = new GetPublicTraceabilityEventsByBatchIdQuery(batchId);
        var events = traceabilityQueryService.handle(query);

        // 2. Extraemos los IDs de los actores para enriquecer los nombres
        List<UUID> actorIds = events.stream()
                .map(TraceabilityEvent::getActorId)
                .distinct()
                .toList();
        Map<UUID, String> actorNames = userQueryService.getUsernamesForIds(actorIds);

        // 3. Filtramos, mapeamos y ordenamos
        var routePoints = events.stream()
                // Nos quedamos solo con los eventos que tienen una ubicación válida
                .filter(event -> event.getLocation() != null && event.getLocation().getLatitude() != null && event.getLocation().getLongitude() != null)
                // Ordenamos por fecha para que la línea del mapa sea coherente
                .sorted(java.util.Comparator.comparing(TraceabilityEvent::getEventDate))
                // Convertimos cada evento a nuestro DTO de punto de ruta
                .map(event -> RoutePointResourceFromEntityAssembler.toResourceFromEntity(
                        event,
                        actorNames.get(event.getActorId())
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(routePoints);
    }
}