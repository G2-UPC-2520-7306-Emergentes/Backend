// application/internal/commandservices/TraceabilityCommandServiceImpl.java
package com.foodchain.traceability_context.application.internal.commandservices;

import com.foodchain.traceability_context.application.outbound.geolocation.GeocodingService;
import com.foodchain.traceability_context.application.outbound.hashing.HashingService;
import com.foodchain.traceability_context.application.outbound.messaging.events.EventPublisher;
import com.foodchain.traceability_context.application.outbound.messaging.events.StepRegisteredEvent;
import com.foodchain.traceability_context.application.outbound.storage.FileStorageService;
import com.foodchain.traceability_context.domain.model.commands.CorrectTraceabilityEventCommand;
import com.foodchain.traceability_context.domain.model.commands.RegisterTraceabilityEventCommand;
import com.foodchain.traceability_context.domain.model.entities.TraceabilityEvent;
import com.foodchain.traceability_context.domain.model.valueobjects.Location;
import com.foodchain.traceability_context.domain.repository.TraceabilityRepository;
import com.foodchain.traceability_context.domain.services.TraceabilityCommandService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

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
    public TraceabilityEvent handle(RegisterTraceabilityEventCommand command) {

        traceabilityRepository.findLatestByBatchId(command.batchId()).ifPresent(latestEvent -> {
            // Creamos una fecha "ahora" para comparar. Será muy cercana a la que se creará en la entidad.
            Date now = new Date();
            if (now.before(latestEvent.getEventDate())) {
                // Este caso es muy raro y podría indicar un problema con el reloj del servidor.
                throw new IllegalStateException("Error de coherencia temporal: El nuevo evento no puede ser anterior al último evento registrado.");
            }
        });

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
                enrichedLocation, proofImageUrl, proofImageHash, command.clientCreatedAt());

        // 3. Guardar en la base de datos
        traceabilityRepository.save(event);

        // 4. Crear y publicar el evento para RabbitMQ
        eventPublisher.publish(mapToStepRegisteredEvent(event));

        return event; // ¡Devolvemos la entidad completa!
    }

    @Override
    @Transactional
    public TraceabilityEvent handle(CorrectTraceabilityEventCommand command) {

        // 1. Validar que el evento original existe
        var originalEvent = traceabilityRepository.findById(command.originalEventId())
                .orElseThrow(() -> new EntityNotFoundException("El evento original a corregir no existe: " + command.originalEventId()));

        // 2. Orquestar hashing y almacenamiento del fichero de prueba (si existe)
        String proofImageUrl = null;
        String proofImageHash = null;
        if (command.proofImageFile() != null && !command.proofImageFile().isEmpty()) {
            proofImageHash = hashingService.hashFile(command.proofImageFile());
            proofImageUrl = fileStorageService.store(command.proofImageFile());
        }

        // 3. Enriquecer la ubicación
        Location enrichedLocation = geocodingService.reverseGeocode(command.latitude(), command.longitude());

        // 4. Usar el nuevo factory method para crear el evento de corrección
        var correctionEvent = TraceabilityEvent.recordCorrection(
                command.originalEventId(),
                originalEvent.getBatchId(), // El batchId es el mismo que el del evento original
                command.actorId(),
                enrichedLocation,
                command.justification(),
                proofImageUrl,
                proofImageHash
        );

        // 5. Guardar el NUEVO evento de corrección
        traceabilityRepository.save(correctionEvent);

        // 6. Publicar el evento de corrección en la cola para que también sea anclado
        // (La lógica para crear el StepRegisteredEvent DTO es la misma que para un evento normal)
        eventPublisher.publish(mapToStepRegisteredEvent(correctionEvent));

        return correctionEvent; // ¡Devolvemos la entidad completa!
    }

    /**
     * Método de utilidad privado para mapear una entidad TraceabilityEvent a su DTO de evento de dominio.
     * Esto centraliza la lógica de conversión y evita la duplicación de código.
     *
     * @param event La entidad de dominio persistida.
     * @return El DTO StepRegisteredEvent listo para ser publicado en la cola de mensajes.
     */
    private StepRegisteredEvent mapToStepRegisteredEvent(TraceabilityEvent event) {
        // Mapea el Value Object Location a su DTO correspondiente
        var locationDto = new StepRegisteredEvent.LocationDTO(
                event.getLocation().getLatitude(),
                event.getLocation().getLongitude(),
                event.getLocation().getAddress(),
                event.getLocation().getCity(),
                event.getLocation().getCountry()
        );

        // Construye el DTO principal del evento, ahora incluyendo el hash del comprobante
        return new StepRegisteredEvent(
                event.getId(),
                event.getBatchId(),
                event.getEventType(),
                event.getEventDate(),
                event.getActorId(),
                locationDto,
                event.getProofImageHash() // ¡Añadimos el hash de la imagen al evento!
        );
    }
}