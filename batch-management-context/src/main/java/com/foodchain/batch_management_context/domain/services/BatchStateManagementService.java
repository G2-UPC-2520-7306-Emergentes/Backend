// domain/services/BatchStateManagementService.java
package com.foodchain.batch_management_context.domain.services;

import com.foodchain.shared_domain.events.StepRegisteredEvent;

public interface BatchStateManagementService {
    void processStepEvent(StepRegisteredEvent event);
}