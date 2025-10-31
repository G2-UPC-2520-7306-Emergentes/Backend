// interfaces/rest/TraceabilityQueryController.java
package com.foodchain.traceability_context.interfaces.rest;

import com.foodchain.traceability_context.domain.model.queries.GetHistoryByBatchIdQuery;
import com.foodchain.traceability_context.domain.services.TraceabilityQueryService;
import com.foodchain.traceability_context.interfaces.rest.resources.TraceabilityHistoryResource;
import com.foodchain.traceability_context.interfaces.rest.transform.TraceabilityHistoryResourceFromEntityAssembler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/trace/history")
public class TraceabilityQueryController {
    private final TraceabilityQueryService traceabilityQueryService;

    public TraceabilityQueryController(TraceabilityQueryService traceabilityQueryService) {
        this.traceabilityQueryService = traceabilityQueryService;
    }

    @GetMapping("/{batchId}")
    public ResponseEntity<TraceabilityHistoryResource> getHistoryByBatchId(@PathVariable UUID batchId) {
        var query = new GetHistoryByBatchIdQuery(batchId);
        var events = traceabilityQueryService.handle(query);

        if (events.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        var resource = TraceabilityHistoryResourceFromEntityAssembler.toResourceFromEntities(batchId, events);
        return ResponseEntity.ok(resource);
    }
}