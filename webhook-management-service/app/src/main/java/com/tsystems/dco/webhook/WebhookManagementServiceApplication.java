package com.tsystems.dco.webhook;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableRabbit
@ComponentScan(basePackages = {"com.tsystems.dco.webhook", "com.tsystems.dco.config"})
public class WebhookManagementServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebhookManagementServiceApplication.class, args);
    }
}
