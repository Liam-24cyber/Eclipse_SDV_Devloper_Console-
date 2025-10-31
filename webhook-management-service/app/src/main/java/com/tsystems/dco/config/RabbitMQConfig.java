package com.tsystems.dco.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String SCENARIO_EVENTS_QUEUE = "scenario.events";
    public static final String TRACK_EVENTS_QUEUE = "track.events";
    public static final String SIMULATION_EVENTS_QUEUE = "simulation.events";

    @Bean
    public Queue scenarioEventsQueue() {
        return QueueBuilder.durable(SCENARIO_EVENTS_QUEUE).build();
    }

    @Bean
    public Queue trackEventsQueue() {
        return QueueBuilder.durable(TRACK_EVENTS_QUEUE).build();
    }

    @Bean
    public Queue simulationEventsQueue() {
        return QueueBuilder.durable(SIMULATION_EVENTS_QUEUE).build();
    }
}
