// domain/model/queries/GetHistoryByBatchIdQuery.java
package com.foodchain.traceability_context.domain.model.queries;

import java.util.UUID;

/**
 * Consulta para obtener el historial completo de eventos de un lote específico.
 */
public record GetHistoryByBatchIdQuery(UUID batchId) {
}