// application/internal/commandservices/TraceabilityCommandServiceImpl.java
package com.foodchain.traceability_context.application.internal.commandservices;

import com.foodchain.traceability_context.application.outbound.geolocation.GeocodingService;
import com.foodchain.traceability_context.application.outbound.hashing.HashingService;
import com.foodchain.traceability_context.application.outbound.messaging.events.EventPublisher;
import com.foodchain.traceability_context.application.outbound.messaging.events.StepRegisteredEvent;
import com.foodchain.traceability_context.application.outbound.storage.FileStorageService;
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
    private final GeocodingService geocodingService;
    private final HashingService hashingService;
    private final FileStorageService fileStorageService;

    public TraceabilityCommandServiceImpl(TraceabilityRepository r, EventPublisher<StepRegisteredEvent> p,
                                          GeocodingService g, HashingService h, FileStorageService f) {
        this.traceabilityRepository = r;
        this.eventPublisher = p;
        this.geocodingService = g;
        this.hashingService = h;
        this.fileStorageService = f;
    }

    @Override
    @Transactional
    public UUID handle(RegisterTraceabilityEventCommand command) {
        // 1. Enriquecer la ubicación llamando al servicio de infraestructura
        Location enrichedLocation = geocodingService.reverseGeocode(command.latitude(), command.longitude());

        /* String expectedCountry = batchQueryService.getCountryForBatch(command.batchId());
        if (!enrichedLocation.getCountry().equals(expectedCountry)) {
             throw new IllegalStateException("La ubicación del evento no coincide con el país de origen del lote.");
        } */

        String proofImageUrl = null;
        String proofImageHash = null;

        if (command.proofImageFile() != null && !command.proofImageFile().isEmpty()) {
            proofImageHash = hashingService.hashFile(command.proofImageFile());
            proofImageUrl = fileStorageService.store(command.proofImageFile());
        }

        // 2. Crear la entidad de dominio con la ubicación ya enriquecida
        var event = TraceabilityEvent.record(command.batchId(), command.eventType(), command.actorId(),
                enrichedLocation, proofImageUrl, proofImageHash);

        // 3. Guardar en la base de datos
        traceabilityRepository.save(event);

        // 4. Crear y publicar el evento para RabbitMQ
        // ... (la lógica de publicación no cambia
        var locationDto = new StepRegisteredEvent.LocationDTO(
                event.getLocation().getLatitude(),
                event.getLocation().getLongitude(),
                event.getLocation().getAddress(), // Añadimos los nuevos campos
                event.getLocation().getCity(),
                event.getLocation().getCountry()
        );

        // ... (construir el StepRegisteredEvent y publicarlo)
        var eventToPublish = new StepRegisteredEvent(
                event.getId(),
                event.getBatchId(),
                event.getEventType(),
                event.getEventDate(),
                event.getActorId(),
                locationDto
        );

        eventPublisher.publish(eventToPublish);

        return event.getId();
    }
}