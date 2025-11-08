// interfaces/messaging/TraceabilityEventListener.java
package com.foodchain.batch_management_context.interfaces.messaging;

import com.foodchain.batch_management_context.domain.services.BatchStateManagementService;
import com.foodchain.shared_domain.events.StepRegisteredEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class TraceabilityEventListener {

    private final BatchStateManagementService batchStateManagementService;

    public TraceabilityEventListener(BatchStateManagementService batchStateManagementService) {
        this.batchStateManagementService = batchStateManagementService;
    }

    @RabbitListener(queues = "foodchain-traceability-queue")
    public void onStepRegistered(StepRegisteredEvent event) {
        // El listener solo delega la lógica al servicio de aplicación.
        batchStateManagementService.processStepEvent(event);
    }
}