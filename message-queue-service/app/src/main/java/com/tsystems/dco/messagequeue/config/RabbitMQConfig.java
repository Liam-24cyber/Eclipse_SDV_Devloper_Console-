package com.tsystems.dco.messagequeue.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ configuration for the Message Queue Integration Service.
 * 
 * Configures exchanges, queues, bindings, and message converters
 * for the SDV Developer Console event-driven architecture.
 */
@Configuration
@EnableRabbit
@AutoConfigureBefore(RabbitAutoConfiguration.class)
public class RabbitMQConfig {

    // Exchange names
    public static final String SDV_EVENTS_EXCHANGE = "sdv.events";
    public static final String SDV_DLX_EXCHANGE = "sdv.dlx";
    
    // Queue names
    public static final String SCENARIO_EVENTS_QUEUE = "scenario.events";
    public static final String TRACK_EVENTS_QUEUE = "track.events";
    public static final String SIMULATION_EVENTS_QUEUE = "simulation.events";
    public static final String WEBHOOK_EVENTS_QUEUE = "webhook.events";
    
    // Dead letter queues
    public static final String SCENARIO_DLQ = "scenario.events.dlq";
    public static final String TRACK_DLQ = "track.events.dlq";
    public static final String SIMULATION_DLQ = "simulation.events.dlq";
    public static final String WEBHOOK_DLQ = "webhook.events.dlq";
    
    // Routing keys
    public static final String SCENARIO_ROUTING_KEY = "scenario.*";
    public static final String TRACK_ROUTING_KEY = "track.*";
    public static final String SIMULATION_ROUTING_KEY = "simulation.*";
    public static final String WEBHOOK_ROUTING_KEY = "webhook.*";

    @Value("${spring.rabbitmq.host:rabbitmq}")
    private String rabbitHost;

    @Value("${spring.rabbitmq.port:5672}")
    private int rabbitPort;

    @Value("${spring.rabbitmq.username:admin}")
    private String rabbitUsername;

    @Value("${spring.rabbitmq.password:admin123}")
    private String rabbitPassword;

    /**
     * JSON message converter for RabbitMQ messages.
     * Uses the globally configured ObjectMapper from JacksonConfig.
     */
    @Bean
    public MessageConverter jsonMessageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    /**
     * RabbitAdmin bean to enable automatic declaration of exchanges, queues, and bindings.
     * This ensures all RabbitMQ resources are created when the service starts.
     */
    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    /**
     * RabbitTemplate with JSON message converter
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }

    /**
     * Rabbit listener container factory with JSON converter
     */
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory, 
            MessageConverter messageConverter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);
        return factory;
    }

    // ============= EXCHANGES =============

    /**
     * Main SDV events exchange (topic exchange)
     */
    @Bean
    public TopicExchange sdvEventsExchange() {
        return new TopicExchange(SDV_EVENTS_EXCHANGE, true, false);
    }

    /**
     * Dead letter exchange
     */
    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(SDV_DLX_EXCHANGE, true, false);
    }

    // ============= QUEUES =============

    /**
     * Scenario events queue with dead letter configuration
     */
    @Bean
    public Queue scenarioEventsQueue() {
        return QueueBuilder.durable(SCENARIO_EVENTS_QUEUE)
                .withArgument("x-dead-letter-exchange", SDV_DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", SCENARIO_DLQ)
                .withArgument("x-message-ttl", 3600000) // 1 hour TTL
                .build();
    }

    /**
     * Track events queue with dead letter configuration
     */
    @Bean
    public Queue trackEventsQueue() {
        return QueueBuilder.durable(TRACK_EVENTS_QUEUE)
                .withArgument("x-dead-letter-exchange", SDV_DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", TRACK_DLQ)
                .withArgument("x-message-ttl", 3600000) // 1 hour TTL
                .build();
    }

    /**
     * Simulation events queue with dead letter configuration
     */
    @Bean
    public Queue simulationEventsQueue() {
        return QueueBuilder.durable(SIMULATION_EVENTS_QUEUE)
                .withArgument("x-dead-letter-exchange", SDV_DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", SIMULATION_DLQ)
                .withArgument("x-message-ttl", 3600000) // 1 hour TTL
                .build();
    }

    /**
     * Webhook events queue with dead letter configuration
     */
    @Bean
    public Queue webhookEventsQueue() {
        return QueueBuilder.durable(WEBHOOK_EVENTS_QUEUE)
                .withArgument("x-dead-letter-exchange", SDV_DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", WEBHOOK_DLQ)
                .withArgument("x-message-ttl", 3600000) // 1 hour TTL
                .build();
    }

    // ============= DEAD LETTER QUEUES =============

    @Bean
    public Queue scenarioDeadLetterQueue() {
        return QueueBuilder.durable(SCENARIO_DLQ).build();
    }

    @Bean
    public Queue trackDeadLetterQueue() {
        return QueueBuilder.durable(TRACK_DLQ).build();
    }

    @Bean
    public Queue simulationDeadLetterQueue() {
        return QueueBuilder.durable(SIMULATION_DLQ).build();
    }

    @Bean
    public Queue webhookDeadLetterQueue() {
        return QueueBuilder.durable(WEBHOOK_DLQ).build();
    }

    // ============= BINDINGS =============

    @Bean
    public Binding scenarioEventsBinding() {
        return BindingBuilder
                .bind(scenarioEventsQueue())
                .to(sdvEventsExchange())
                .with(SCENARIO_ROUTING_KEY);
    }

    @Bean
    public Binding trackEventsBinding() {
        return BindingBuilder
                .bind(trackEventsQueue())
                .to(sdvEventsExchange())
                .with(TRACK_ROUTING_KEY);
    }

    @Bean
    public Binding simulationEventsBinding() {
        return BindingBuilder
                .bind(simulationEventsQueue())
                .to(sdvEventsExchange())
                .with(SIMULATION_ROUTING_KEY);
    }

    @Bean
    public Binding webhookEventsBinding() {
        return BindingBuilder
                .bind(webhookEventsQueue())
                .to(sdvEventsExchange())
                .with(WEBHOOK_ROUTING_KEY);
    }

    // ============= DEAD LETTER BINDINGS =============

    @Bean
    public Binding scenarioDeadLetterBinding() {
        return BindingBuilder
                .bind(scenarioDeadLetterQueue())
                .to(deadLetterExchange())
                .with(SCENARIO_DLQ);
    }

    @Bean
    public Binding trackDeadLetterBinding() {
        return BindingBuilder
                .bind(trackDeadLetterQueue())
                .to(deadLetterExchange())
                .with(TRACK_DLQ);
    }

    @Bean
    public Binding simulationDeadLetterBinding() {
        return BindingBuilder
                .bind(simulationDeadLetterQueue())
                .to(deadLetterExchange())
                .with(SIMULATION_DLQ);
    }

    @Bean
    public Binding webhookDeadLetterBinding() {
        return BindingBuilder
                .bind(webhookDeadLetterQueue())
                .to(deadLetterExchange())
                .with(WEBHOOK_DLQ);
    }
}
