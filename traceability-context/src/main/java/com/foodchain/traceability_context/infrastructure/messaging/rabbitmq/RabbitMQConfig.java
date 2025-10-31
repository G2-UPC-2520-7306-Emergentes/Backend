// traceability-context/infrastructure/messaging/rabbitmq/RabbitMQConfig.java
package com.foodchain.traceability_context.infrastructure.messaging.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Nombres de nuestra infraestructura de mensajería
    private static final String EXCHANGE_NAME = "foodchain-exchange";
    private static final String QUEUE_NAME = "foodchain-traceability-queue";
    private static final String ROUTING_KEY = "traceability.event.registered";

    /**
     * Define el Exchange (la "oficina de correos") a la que enviaremos los mensajes.
     * Un TopicExchange permite un enrutamiento flexible basado en patrones.
     */
    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    /**
     * Define la Queue (el "buzón") donde se almacenarán los mensajes.
     * 'true' indica que la cola es durable (sobrevive a reinicios del broker).
     */
    @Bean
    public Queue queue() {
        return new Queue(QUEUE_NAME, true);
    }

    /**
     * Define el Binding (la "regla de enrutamiento") que conecta el Exchange con la Queue.
     * Le dice al exchange: "Cualquier mensaje que recibas con la dirección (routing key)
     * 'traceability.event.registered', envíalo a esta cola".
     */
    @Bean
    public Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY);
    }

    /**
     * Define el convertidor de mensajes para que Spring sepa cómo manejar JSON.
     * Este bean es crucial para evitar el error de 'SimpleMessageConverter'.
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}