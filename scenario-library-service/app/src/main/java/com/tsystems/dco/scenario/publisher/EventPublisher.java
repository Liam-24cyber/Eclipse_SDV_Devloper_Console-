package com.tsystems.dco.scenario.publisher;

import com.tsystems.dco.common.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class EventPublisher {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void publishScenarioEvent(String eventType, String scenarioId, Map<String, Object> data) {
        data.put("eventType", eventType);
        data.put("eventId", scenarioId);
        data.put("scenarioId", scenarioId);
        data.put("timestamp", System.currentTimeMillis());
        
        // Determine routing key based on event type
        String routingKey = getRoutingKeyForEventType(eventType);
        
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.EVENTS_EXCHANGE, 
            routingKey, 
            data
        );
    }
    
    private String getRoutingKeyForEventType(String eventType) {
        switch (eventType) {
            case "CREATED":
                return RabbitMQConfig.SCENARIO_CREATED_ROUTING_KEY;
            case "UPDATED":
                return RabbitMQConfig.SCENARIO_UPDATED_ROUTING_KEY;
            case "DELETED":
                return RabbitMQConfig.SCENARIO_DELETED_ROUTING_KEY;
            default:
                return RabbitMQConfig.SCENARIO_UPDATED_ROUTING_KEY;
        }
    }
}
