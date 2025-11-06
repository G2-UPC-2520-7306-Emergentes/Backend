// application/outbound/traceability/TraceabilityService.java
package com.foodchain.batch_management_context.application.outbound.traceability;

import java.util.UUID;

public interface TraceabilityService {
    /**
     * Verifica si un lote tiene algún evento de trazabilidad registrado.
     * @param batchId El ID del lote a verificar.
     * @return true si tiene al menos un evento, false en caso contrario.
     */
    boolean hasTraceabilityEvents(UUID batchId);
}