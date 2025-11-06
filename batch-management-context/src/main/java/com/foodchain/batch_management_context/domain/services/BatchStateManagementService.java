// domain/services/BatchStateManagementService.java
package com.foodchain.batch_management_context.domain.services;
import com.foodchain.batch_management_context.application.internal.events.StepRegisteredEvent;

public interface BatchStateManagementService {
    void processStepEvent(StepRegisteredEvent event);
}