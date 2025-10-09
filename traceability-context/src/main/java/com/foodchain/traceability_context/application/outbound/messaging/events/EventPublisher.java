// application/outbound/messaging/events/EventPublisher.java
package com.foodchain.traceability_context.application.outbound.messaging.events;

// Usamos un tipo genérico para que este publicador pueda reutilizarse para cualquier evento
public interface EventPublisher<T> {
    void publish(T event);
}