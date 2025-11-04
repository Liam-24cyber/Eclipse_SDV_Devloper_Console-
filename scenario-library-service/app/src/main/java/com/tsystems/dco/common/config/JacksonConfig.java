package com.tsystems.dco.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.core.Ordered;

/**
 * Global Jackson configuration to ensure proper serialization of Java 8 date/time types.
 * 
 * This configuration creates a primary ObjectMapper bean with JSR310 module support,
 * ensuring that LocalDateTime, LocalDate, and other Java 8 temporal types are properly
 * serialized to JSON across the entire application, including REST API responses and
 * RabbitMQ message conversion.
 * 
 * The @Order(HIGHEST_PRECEDENCE) ensures this bean is created early in the Spring
 * context lifecycle, before any auto-configured beans that might depend on it.
 */
@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
public class JacksonConfig {
    
    /**
     * Creates a global ObjectMapper bean with JSR310 support.
     * 
     * This ObjectMapper will be used throughout the application for:
     * - REST API request/response serialization
     * - GraphQL query/mutation responses
     * - RabbitMQ message serialization (via Jackson2JsonMessageConverter)
     * - Any other JSON serialization needs
     * 
     * @return ObjectMapper configured with JavaTimeModule
     */
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        
        // Register JSR310 module for Java 8 date/time support
        mapper.registerModule(new JavaTimeModule());
        
        // Write dates as ISO-8601 strings instead of timestamps
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        // Additional configuration for better JSON handling
        mapper.configure(SerializationFeature.WRITE_DATES_WITH_ZONE_ID, false);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        
        return mapper;
    }
}
