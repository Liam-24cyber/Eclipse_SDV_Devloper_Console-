package com.tsystems.dco.common.config;

/**
 * RabbitMQ configuration constants
 */
public class RabbitMQConfig {
    
    // Exchange names
    public static final String EVENTS_EXCHANGE = "scenario.events";
    public static final String SIMULATION_EVENTS_EXCHANGE = "simulation.events";
    
    // Routing keys for scenario events
    public static final String SCENARIO_CREATED_ROUTING_KEY = "scenario.created";
    public static final String SCENARIO_UPDATED_ROUTING_KEY = "scenario.updated";
    public static final String SCENARIO_DELETED_ROUTING_KEY = "scenario.deleted";
    
    // Routing keys for simulation events
    public static final String SIMULATION_CREATED_KEY = "simulation.created";
    public static final String SIMULATION_UPDATED_KEY = "simulation.updated";
    public static final String SIMULATION_DELETED_KEY = "simulation.deleted";
    
    private RabbitMQConfig() {
        // Private constructor to prevent instantiation
    }
}
