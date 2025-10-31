// domain/services/AnchoringCommandService.java
package com.foodchain.blockchain_worker_context.domain.services;

import com.foodchain.blockchain_worker_context.domain.model.commands.AnchorEventCommand;

public interface AnchoringCommandService {
    void handle(AnchorEventCommand command);
}