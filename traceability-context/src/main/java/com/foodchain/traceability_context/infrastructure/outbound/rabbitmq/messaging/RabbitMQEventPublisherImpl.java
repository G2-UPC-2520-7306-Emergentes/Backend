// infrastructure/outbound/messaging/rabbitmq/RabbitMQEventPublisherImpl.java
package com.foodchain.traceability_context.infrastructure.outbound.rabbitmq.messaging;

import com.foodchain.traceability_context.application.outbound.messaging.events.EventPublisher;
import com.foodchain.traceability_context.application.outbound.messaging.events.StepRegisteredEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQEventPublisherImpl implements EventPublisher<StepRegisteredEvent> {

    private final RabbitTemplate rabbitTemplate;
    private static final String EXCHANGE_NAME = "foodchain-exchange";
    private static final String ROUTING_KEY = "traceability.event.registered";

    public RabbitMQEventPublisherImpl(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publish(StepRegisteredEvent event) {
        rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY, event);
    }
}