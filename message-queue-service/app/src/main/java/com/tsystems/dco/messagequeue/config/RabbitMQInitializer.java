package com.tsystems.dco.messagequeue.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * RabbitMQ Initializer Component
 * 
 * This component ensures that all RabbitMQ exchanges, queues, and bindings
 * are created when the application starts. This prevents issues where
 * downstream services try to consume from non-existent queues.
 * 
 * The initialization happens after the application is fully started
 * via the ApplicationReadyEvent listener.
 */
@Component
public class RabbitMQInitializer implements ApplicationListener<ApplicationReadyEvent> {
    
    private static final Logger logger = LoggerFactory.getLogger(RabbitMQInitializer.class);
    
    @Autowired
    private RabbitAdmin rabbitAdmin;
    
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        try {
            logger.info("🚀 Initializing RabbitMQ resources...");
            
            // Force initialization of all AMQP beans (exchanges, queues, bindings)
            rabbitAdmin.initialize();
            
            logger.info("✅ RabbitMQ initialization complete!");
            logger.info("📋 Created resources:");
            logger.info("   - Exchanges: {} (main), {} (DLX)", 
                    RabbitMQConfig.SDV_EVENTS_EXCHANGE, 
                    RabbitMQConfig.SDV_DLX_EXCHANGE);
            logger.info("   - Event Queues: {}, {}, {}, {}", 
                    RabbitMQConfig.SCENARIO_EVENTS_QUEUE,
                    RabbitMQConfig.TRACK_EVENTS_QUEUE,
                    RabbitMQConfig.SIMULATION_EVENTS_QUEUE,
                    RabbitMQConfig.WEBHOOK_EVENTS_QUEUE);
            logger.info("   - Dead Letter Queues: {}, {}, {}, {}",
                    RabbitMQConfig.SCENARIO_DLQ,
                    RabbitMQConfig.TRACK_DLQ,
                    RabbitMQConfig.SIMULATION_DLQ,
                    RabbitMQConfig.WEBHOOK_DLQ);
            logger.info("🎯 RabbitMQ is ready for message processing!");
            
        } catch (Exception e) {
            logger.error("❌ Failed to initialize RabbitMQ resources", e);
            logger.error("⚠️  This will cause downstream services to fail!");
            throw new RuntimeException("RabbitMQ initialization failed", e);
        }
    }
}
