// interfaces/rest/transform/TraceabilityHistoryResourceFromEntityAssembler.java
package com.foodchain.traceability_context.interfaces.rest.transform;

import com.foodchain.traceability_context.domain.model.entities.TraceabilityEvent;
import com.foodchain.traceability_context.interfaces.rest.resources.TraceabilityEventResource;
import com.foodchain.traceability_context.interfaces.rest.resources.TraceabilityHistoryResource;
import com.foodchain.traceability_context.application.outbound.iam.EnterpriseResource; // Importar

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class TraceabilityHistoryResourceFromEntityAssembler {

    // El método ahora necesita recibir toda la información enriquecida desde el controlador
    public static TraceabilityHistoryResource toResourceFromEntities(
            UUID batchId,
            List<TraceabilityEvent> events,
            Map<UUID, String> actorNames,
            Map<UUID, EnterpriseResource> enterpriseDetails,
            String txUrlTemplate) {

        var eventResources = events.stream()
                .map(event -> {
                    // Obtenemos los detalles enriquecidos para este evento específico
                    String actorName = actorNames.get(event.getActorId());
                    // Necesitaríamos también los UserDetails para obtener el enterpriseId
                    // Esto demuestra que esta clase está en el lugar incorrecto.
                    // La orquestación debe hacerse en el controlador.

                    // La llamada correcta usaría el otro assembler
                    return TraceabilityEventResourceFromEntityAssembler.toResourceFromEntity(
                            event,
                            actorName,
                            null, // Necesitaríamos el EnterpriseResource aquí
                            txUrlTemplate
                    );
                })
                .collect(Collectors.toList());

        return new TraceabilityHistoryResource(batchId, eventResources);
    }
}