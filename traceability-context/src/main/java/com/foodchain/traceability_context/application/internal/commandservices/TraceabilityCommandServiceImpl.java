// application/internal/commandservices/TraceabilityCommandServiceImpl.java
package com.foodchain.traceability_context.application.internal.commandservices;

import com.foodchain.traceability_context.application.outbound.messaging.events.EventPublisher;
import com.foodchain.traceability_context.application.outbound.messaging.events.StepRegisteredEvent;
import com.foodchain.traceability_context.domain.model.commands.RegisterTraceabilityEventCommand;
import com.foodchain.traceability_context.domain.model.entities.TraceabilityEvent;
import com.foodchain.traceability_context.domain.model.valueobjects.Location;
import com.foodchain.traceability_context.domain.repository.TraceabilityRepository;
import com.foodchain.traceability_context.domain.services.TraceabilityCommandService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
public class TraceabilityCommandServiceImpl implements TraceabilityCommandService {

    private final TraceabilityRepository traceabilityRepository;
    private final EventPublisher<StepRegisteredEvent> eventPublisher;

    public TraceabilityCommandServiceImpl(TraceabilityRepository traceabilityRepository, EventPublisher<StepRegisteredEvent> eventPublisher) {
        this.traceabilityRepository = traceabilityRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public UUID handle(RegisterTraceabilityEventCommand command) {
        var location = new Location(command.latitude(), command.longitude());
        var event = TraceabilityEvent.record(command.batchId(), command.eventType(), command.actorId(), location);

        traceabilityRepository.save(event);

        // Crea el DTO del evento para enviarlo a la cola
        var locationDto = new StepRegisteredEvent.LocationDTO(event.getLocation().getLatitude(), event.getLocation().getLongitude());
        var stepRegisteredEvent = new StepRegisteredEvent(
                event.getId(),
                event.getBatchId(),
                event.getEventType(),
                event.getEventDate(),
                event.getActorId(),
                locationDto
        );
        // Publica el evento y se olvida. El worker se encargará del resto.
        eventPublisher.publish(stepRegisteredEvent);

        return event.getId();
    }
}