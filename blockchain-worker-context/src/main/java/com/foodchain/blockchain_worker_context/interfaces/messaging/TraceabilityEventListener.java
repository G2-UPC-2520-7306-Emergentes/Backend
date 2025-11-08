package com.foodchain.blockchain_worker_context.interfaces.messaging;

import com.foodchain.blockchain_worker_context.domain.model.commands.AnchorEventCommand;
import com.foodchain.blockchain_worker_context.domain.services.AnchoringCommandService;
import com.foodchain.shared_domain.events.StepRegisteredEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

// interfaces/messaging/TraceabilityEventListener.java
@Component
public class TraceabilityEventListener {

    private final AnchoringCommandService anchoringCommandService;

    public TraceabilityEventListener(AnchoringCommandService anchoringCommandService) {
        this.anchoringCommandService = anchoringCommandService;
    }

    @RabbitListener(queues = "foodchain-traceability-queue")
    public void onStepRegistered(StepRegisteredEvent event) {
        System.out.println("EVENTO RECIBIDO: " + event);

        String hashToAnchor = calculateHash(event);

        var command = new AnchorEventCommand(event.eventId(), hashToAnchor);
        anchoringCommandService.handle(command);
    }

    /**
     * Simula/Calcula el hash SHA-256 de los datos de un evento.
     * ESTA LÓGICA DEBE SER IDÉNTICA a la que se usaría en el productor.
     * @param event El evento recibido.
     * @return Un hash hexadecimal de 64 caracteres (32 bytes).
     */
    private String calculateHash(StepRegisteredEvent event) {
        try {
            // Concatenamos los campos importantes del evento en un String consistente
            String dataToHash = event.eventId().toString() +
                    event.batchId().toString() +
                    event.eventType() +
                    event.eventDate().getTime() + // Usamos el timestamp numérico
                    event.actorId().toString() +
                    event.location().latitude() +
                    event.location().longitude();

            // Usamos el algoritmo SHA-256 estándar de Java
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(dataToHash.getBytes(StandardCharsets.UTF_8));

            // Convertimos el array de bytes a una representación hexadecimal
            StringBuilder hexString = new StringBuilder(2 * encodedhash.length);
            for (byte b : encodedhash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            // Este error no debería ocurrir si SHA-256 está disponible en la JVM
            throw new RuntimeException("No se pudo calcular el hash.", e);
        }
    }
}