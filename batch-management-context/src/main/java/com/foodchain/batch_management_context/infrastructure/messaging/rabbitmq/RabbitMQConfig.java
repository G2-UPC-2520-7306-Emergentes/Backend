package com.foodchain.batch_management_context.infrastructure.messaging.rabbitmq; // ¡OJO! CAMBIA ESTE PACKAGE SEGÚN EL MÓDULO

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
        // Marcamos la cola como durable para que los mensajes sobrevivan a reinicios.
        return new Queue(QUEUE_NAME, true);
    }

    @Bean
    public Binding binding(Queue queue, TopicExchange exchange) {
        // Enlazamos la cola al exchange usando la routing key.
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY);
    }
}