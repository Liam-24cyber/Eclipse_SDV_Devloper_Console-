package com.tsystems.dco.webhook;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main Spring Boot application class for the Webhook Management Service.
 * 
 * This service provides:
 * - Webhook registration and management
 * - Event-driven webhook triggering
 * - Delivery retry mechanisms
 * - Webhook delivery history and analytics
 */
@SpringBootApplication
@EnableAsync
@EnableScheduling
public class WebhookManagementServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebhookManagementServiceApplication.class, args);
    }
}
