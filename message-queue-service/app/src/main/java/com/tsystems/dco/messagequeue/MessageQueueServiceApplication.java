package com.tsystems.dco.messagequeue;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Main Spring Boot application class for the Message Queue Integration Service.
 * 
 * This service provides:
 * - Message publishing and routing via RabbitMQ
 * - Event-driven architecture support
 * - Dead letter queue handling
 * - Message transformation and filtering
 */
@SpringBootApplication
@EnableAsync
public class MessageQueueServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MessageQueueServiceApplication.class, args);
    }
}
