package com.tsystems.dco.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * RabbitMQ Configuration
 * 
 * This configuration handles message deserialization for the webhook service.
 * The actual queues, exchanges, and bindings are created by the 
 * message-queue-service to ensure they exist before this service starts.
 */
@Configuration
public class RabbitMQConfig {

    // Queue name constants - queues are created by message-queue-service
    public static final String SCENARIO_EVENTS_QUEUE = "scenario.events";
    public static final String TRACK_EVENTS_QUEUE = "track.events";
    public static final String SIMULATION_EVENTS_QUEUE = "simulation.events";

    /**
     * Configure Jackson message converter to handle deserialization
     * of messages from RabbitMQ, including those with custom type headers
     */
    @Bean
    public Jackson2JsonMessageConverter messageConverter(ObjectMapper objectMapper) {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter(objectMapper);
        converter.setClassMapper(classMapper());
        return converter;
    }

    /**
     * Configure class mapper to handle type conversion
     * Maps all incoming messages to Map<String, Object> by default
     * This allows the webhook service to receive messages without
     * needing the exact same type definitions as the publishing service
     */
    @Bean
    public DefaultClassMapper classMapper() {
        DefaultClassMapper classMapper = new DefaultClassMapper();
        // Trust messages from our internal services
        classMapper.setTrustedPackages("com.tsystems.dco.*", "java.util.*", "java.lang.*");
        // Map the DomainEvent type to Map for compatibility
        classMapper.setIdClassMapping(Map.of(
            "com.tsystems.dco.messagequeue.service.MessagePublishingService$DomainEvent", Map.class
        ));
        // Default all messages to Map for flexibility
        classMapper.setDefaultType(Map.class);
        return classMapper;
    }

    /**
     * Configure RabbitMQ listener container factory with our custom message converter
     * This ensures all @RabbitListener methods receive properly deserialized messages
     */
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter messageConverter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);
        return factory;
    }
}
