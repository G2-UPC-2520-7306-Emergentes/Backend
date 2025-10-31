// blockchain-worker-context/infrastructure/messaging/rabbitmq/RabbitMQConfig.java
package com.foodchain.blockchain_worker_context.infrastructure.messaging.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMQConfig {

    private static final String EXCHANGE_NAME = "foodchain-exchange";
    private static final String QUEUE_NAME = "foodchain-traceability-queue";
    private static final String ROUTING_KEY = "traceability.event.registered";

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue queue() {
        return new Queue(QUEUE_NAME, true);
    }

    @Bean
    public Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY);
    }

    /**
     * Define el convertidor de mensajes JSON con un mapa de traducción de tipos explícito.
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();

        // --- ¡LA MAGIA ESTÁ AQUÍ! ---
        DefaultClassMapper classMapper = new DefaultClassMapper();
        Map<String, Class<?>> idClassMapping = new HashMap<>();

        // Le decimos a Spring: "Cuando en la cabecera '__TypeId__' veas este String..."
        String producerEventClassId = "com.foodchain.traceability_context.application.outbound.messaging.events.StepRegisteredEvent";

        // "... quiero que lo mapees a ESTA clase local."
        Class<?> consumerEventClass = com.foodchain.blockchain_worker_context.application.internal.events.StepRegisteredEvent.class;

        idClassMapping.put(producerEventClassId, consumerEventClass);

        classMapper.setIdClassMapping(idClassMapping);
        converter.setClassMapper(classMapper);

        return converter;
    }
}