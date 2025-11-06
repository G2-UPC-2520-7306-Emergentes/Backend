// EN: traceability-context/src/main/java/com/foodchain/traceability_context/interfaces/rest/TraceabilityQueryController.java
package com.foodchain.traceability_context.interfaces.rest;

import com.foodchain.shared_domain.domain.model.aggregates.UserDetails;
import com.foodchain.traceability_context.application.outbound.iam.EnterpriseQueryService;
import com.foodchain.traceability_context.application.outbound.iam.EnterpriseResource;
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
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.Collections;

@RestController
@RequestMapping("/api/v1/trace/history")
@Tag(name = "Traceability Queries", description = "API para la consulta pública y privada de historiales de trazabilidad.")
public class TraceabilityQueryController {

    private final TraceabilityQueryService traceabilityQueryService;
    private final UserQueryService userQueryService;
    private final EnterpriseQueryService enterpriseQueryService;
    private final String txUrlTemplate;

    public TraceabilityQueryController(TraceabilityQueryService tqs, UserQueryService uqs, EnterpriseQueryService eqs,
                                       @Value("${blockchain.explorer.tx-url-template}") String txUrlTemplate) {
        this.traceabilityQueryService = tqs;
        this.userQueryService = uqs;
        this.enterpriseQueryService = eqs;
        this.txUrlTemplate = txUrlTemplate;
    }

    @Operation(summary = "Obtener historial de un lote (Privado)",
            description = "Endpoint para actores de la cadena de suministro. Devuelve el historial paginado de un lote, verificando que el usuario autenticado pertenezca a la empresa propietaria del lote.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historial paginado recuperado exitosamente."),
            @ApiResponse(responseCode = "403", description = "Acceso denegado. El usuario no tiene permisos para ver este lote."),
            @ApiResponse(responseCode = "404", description = "Lote no encontrado.")
    })
    @GetMapping("/batch/{batchId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<TraceabilityEventResource>> getEventsForActor(
            @Parameter(description = "ID del lote a consultar") @PathVariable UUID batchId,
            @AuthenticationPrincipal UserDetails userDetails,
            Pageable pageable) {

        var query = new GetTraceabilityEventsByBatchIdQuery(batchId, userDetails.enterpriseId());
        var eventsPage = traceabilityQueryService.handle(query, pageable);
        var resourcesPage = enrichEventsPage(eventsPage);

        return ResponseEntity.ok(resourcesPage);
    }

    @Operation(summary = "Obtener historial de un lote (Público)",
            description = "Endpoint para consumidores finales. Devuelve el historial paginado de un lote. Solo funciona si el lote está en un estado público (ej. 'FOR_SALE' o 'CLOSED').")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historial paginado recuperado exitosamente."),
            @ApiResponse(responseCode = "403", description = "Acceso denegado. La información de trazabilidad para este lote aún no es pública."),
            @ApiResponse(responseCode = "404", description = "Lote no encontrado.")
    })
    @GetMapping("/public/batch/{batchId}")
    public ResponseEntity<Page<TraceabilityEventResource>> getPublicEvents(
            @Parameter(description = "ID del lote escaneado en el código QR") @PathVariable UUID batchId,
            Pageable pageable) {

        var query = new GetPublicTraceabilityEventsByBatchIdQuery(batchId);
        var eventsPage = traceabilityQueryService.handle(query, pageable);
        var resourcesPage = enrichEventsPage(eventsPage);

        return ResponseEntity.ok(resourcesPage);
    }

    @Operation(summary = "Obtener la ruta geográfica de un lote (Público)",
            description = "Devuelve una lista COMPLETA y ordenada de todos los eventos que tienen coordenadas GPS, ideal para dibujar el mapa del recorrido. Aplica las mismas reglas de visibilidad pública que el historial.")
    @GetMapping("/public/batch/{batchId}/route")
    public ResponseEntity<List<RoutePointResource>> getPublicRoute(@PathVariable UUID batchId) {

        var query = new GetPublicTraceabilityEventsByBatchIdQuery(batchId);
        // LLAMAMOS AL MÉTODO NO PAGINADO DEL SERVICIO
        var events = traceabilityQueryService.handle(query);

        if (events == null || events.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        // REUTILIZAMOS LA LÓGICA DE ENRIQUECIMIENTO
        var actorIds = events.stream().map(TraceabilityEvent::getActorId).distinct().toList();
        // Usamos el método correcto que devuelve UserDetails completos
        var userDetailsMap = userQueryService.getUserDetailsForIds(actorIds);

        var routePoints = events.stream()
                .filter(event -> event.getLocation() != null && event.getLocation().getLatitude() != null)
                .sorted(java.util.Comparator.comparing(TraceabilityEvent::getEventDate))
                .map(event -> {
                    UserDetails details = userDetailsMap.get(event.getActorId());
                    String actorName = (details != null) ? details.email() : "Desconocido";
                    return RoutePointResourceFromEntityAssembler.toResourceFromEntity(event, actorName);
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(routePoints);
    }

     /**
     * Orquesta el enriquecimiento de una página de eventos con datos de otros microservicios.
     * Realiza 2 llamadas de red eficientes (una para usuarios, una para empresas) por cada página.
     */
    private Page<TraceabilityEventResource> enrichEventsPage(Page<TraceabilityEvent> eventsPage) {
        if (eventsPage == null || !eventsPage.hasContent()) {
            assert eventsPage != null;
            return Page.empty(eventsPage.getPageable());
        }

        // 1. Obtener detalles completos de los usuarios (actores)
        List<UUID> actorIds = eventsPage.getContent().stream().map(TraceabilityEvent::getActorId).distinct().toList();
        Map<UUID, UserDetails> userDetailsMap = userQueryService.getUserDetailsForIds(actorIds);

        // 2. A partir de los detalles de usuario, obtener los IDs únicos de las empresas
        List<UUID> enterpriseIds = userDetailsMap.values().stream().map(UserDetails::enterpriseId).distinct().toList();
        Map<UUID, EnterpriseResource> enterpriseDetailsMap = enterpriseQueryService.getEnterprisesByIds(enterpriseIds);

        // 3. Mapear la página de eventos a recursos, inyectando toda la información enriquecida
        return eventsPage.map(event -> {
            UserDetails userDetails = userDetailsMap.get(event.getActorId());
            EnterpriseResource enterprise = (userDetails != null) ? enterpriseDetailsMap.get(userDetails.enterpriseId()) : null;

            return TraceabilityEventResourceFromEntityAssembler.toResourceFromEntity(
                    event,
                    (userDetails != null) ? userDetails.email() : null,
                    enterprise,
                    this.txUrlTemplate
            );
        });
    }
}