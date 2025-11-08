// EN: shared-domain/src/main/java/com/foodchain/shared_domain/events/StepRegisteredEvent.java
package com.foodchain.shared_domain.events;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

public record StepRegisteredEvent(
        UUID eventId,
        UUID batchId,
        String eventType,
        Date eventDate,
        UUID actorId,
        LocationDTO location,
        String proofImageHash
) implements Serializable {

    public record LocationDTO(
            Double latitude,
            Double longitude,
            String address,
            String city,
            String country
    ) implements Serializable {}
}