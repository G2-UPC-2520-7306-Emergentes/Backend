// application/internal/commandservices/BatchStateManagementServiceImpl.java
package com.foodchain.batch_management_context.application.internal.commandservices;

import com.foodchain.batch_management_context.domain.model.valueobjects.BatchId;
import com.foodchain.batch_management_context.domain.repositories.BatchRepository;
import com.foodchain.batch_management_context.domain.services.BatchStateManagementService;
import com.foodchain.shared_domain.events.StepRegisteredEvent;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BatchStateManagementServiceImpl implements BatchStateManagementService {

    private final BatchRepository batchRepository;

    public BatchStateManagementServiceImpl(BatchRepository batchRepository) {
        this.batchRepository = batchRepository;
    }

    @Override
    @Transactional
    public void processStepEvent(StepRegisteredEvent event) {
        var batch = batchRepository.findById(new BatchId(event.batchId()))
                .orElseThrow(() -> new EntityNotFoundException("Lote no encontrado para el evento: " + event.batchId()));

        switch (event.eventType()) {
            // Eventos de Procesamiento (ya implementados)
            case "PROCESAMIENTO_INICIADO":
                batch.startProcessing();
                break;
            case "PROCESAMIENTO_COMPLETADO":
                batch.finishProcessing();
                break;

            // --- ¡NUEVOS EVENTOS PARA US12! ---

            // Eventos de Empaque
            case "EMPAQUE_INICIADO":
                batch.startPacking();
                break;
            case "EMPAQUE_COMPLETADO":
                batch.finishPacking();
                break;

            // Eventos de Distribución
            case "DESPACHO_DESDE_PLANTA": // O "SALIDA_DE_ALMACEN", etc.
                batch.shipToDistributor();
                break;
            case "RECEPCION_EN_CENTRO_DISTRIBUCION": // O "LLEGADA_A_DESTINO"
                batch.receiveInWarehouse();
                break;

            // Eventos de Retail
            case "DISPONIBLE_PARA_VENTA": // O "LLEGADA_A_TIENDA"
                batch.markForSale();
                break;

            default:
                System.out.println("Evento '" + event.eventType() + "' recibido, no requiere cambio de estado del lote.");
                return;
        }

        batchRepository.save(batch);
        System.out.println("Estado del lote " + batch.getBatchId().getValue() + " actualizado a " + batch.getStatus());
    }
}