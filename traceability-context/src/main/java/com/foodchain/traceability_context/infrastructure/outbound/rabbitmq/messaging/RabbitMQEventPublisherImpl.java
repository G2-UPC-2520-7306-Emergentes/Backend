// infrastructure/outbound/messaging/rabbitmq/RabbitMQEventPublisherImpl.java
package com.foodchain.traceability_context.infrastructure.outbound.rabbitmq.messaging;

import com.foodchain.shared_domain.events.StepRegisteredEvent;
import com.foodchain.traceability_context.application.outbound.messaging.events.EventPublisher;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQEventPublisherImpl implements EventPublisher<StepRegisteredEvent> {

    private final RabbitTemplate rabbitTemplate;
    private final String EXCHANGE_NAME;
    private final String ROUTING_KEY;

    public RabbitMQEventPublisherImpl(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        this.EXCHANGE_NAME = "foodchain-exchange";
        this.ROUTING_KEY = "traceability.event.registered";
    }

    @Override
    public void publish(StepRegisteredEvent event) {
        rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY, event);
    }
}