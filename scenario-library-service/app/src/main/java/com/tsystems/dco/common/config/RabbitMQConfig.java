/*
 *   ========================================================================
 *  SDV Developer Console
 *
 *   Copyright (C) 2022 - 2023 T-Systems International GmbH
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 *   SPDX-License-Identifier: Apache-2.0
 *
 *   ========================================================================
 */

package com.tsystems.dco.common.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Exchange
    public static final String EVENTS_EXCHANGE = "dco.events";
    
    // Scenario events
    public static final String SCENARIO_CREATED_QUEUE = "scenario.created";
    public static final String SCENARIO_UPDATED_QUEUE = "scenario.updated";
    public static final String SCENARIO_DELETED_QUEUE = "scenario.deleted";
    public static final String SCENARIO_CREATED_ROUTING_KEY = "scenario.created";
    public static final String SCENARIO_UPDATED_ROUTING_KEY = "scenario.updated";
    public static final String SCENARIO_DELETED_ROUTING_KEY = "scenario.deleted";
    
    // Track events
    public static final String TRACK_CREATED_QUEUE = "track.created";
    public static final String TRACK_UPDATED_QUEUE = "track.updated";
    public static final String TRACK_DELETED_QUEUE = "track.deleted";
    public static final String TRACK_CREATED_ROUTING_KEY = "track.created";
    public static final String TRACK_UPDATED_ROUTING_KEY = "track.updated";
    public static final String TRACK_DELETED_ROUTING_KEY = "track.deleted";

    // Webhook events
    public static final String WEBHOOK_CREATED_QUEUE = "webhook.created";
    public static final String WEBHOOK_UPDATED_QUEUE = "webhook.updated";
    public static final String WEBHOOK_DELETED_QUEUE = "webhook.deleted";
    public static final String WEBHOOK_TRIGGERED_QUEUE = "webhook.triggered";
    public static final String WEBHOOK_CREATED_ROUTING_KEY = "webhook.created";
    public static final String WEBHOOK_UPDATED_ROUTING_KEY = "webhook.updated";
    public static final String WEBHOOK_DELETED_ROUTING_KEY = "webhook.deleted";
    public static final String WEBHOOK_TRIGGERED_ROUTING_KEY = "webhook.triggered";

    // Exchange
    @Bean
    public TopicExchange eventsExchange() {
        return new TopicExchange(EVENTS_EXCHANGE);
    }

    // Scenario Queues
    @Bean
    public Queue scenarioCreatedQueue() {
        return QueueBuilder.durable(SCENARIO_CREATED_QUEUE).build();
    }

    @Bean
    public Queue scenarioUpdatedQueue() {
        return QueueBuilder.durable(SCENARIO_UPDATED_QUEUE).build();
    }

    @Bean
    public Queue scenarioDeletedQueue() {
        return QueueBuilder.durable(SCENARIO_DELETED_QUEUE).build();
    }

    // Scenario Bindings
    @Bean
    public Binding scenarioCreatedBinding(Queue scenarioCreatedQueue, TopicExchange eventsExchange) {
        return BindingBuilder.bind(scenarioCreatedQueue).to(eventsExchange).with(SCENARIO_CREATED_ROUTING_KEY);
    }

    @Bean
    public Binding scenarioUpdatedBinding(Queue scenarioUpdatedQueue, TopicExchange eventsExchange) {
        return BindingBuilder.bind(scenarioUpdatedQueue).to(eventsExchange).with(SCENARIO_UPDATED_ROUTING_KEY);
    }

    @Bean
    public Binding scenarioDeletedBinding(Queue scenarioDeletedQueue, TopicExchange eventsExchange) {
        return BindingBuilder.bind(scenarioDeletedQueue).to(eventsExchange).with(SCENARIO_DELETED_ROUTING_KEY);
    }

    // Track Queues
    @Bean
    public Queue trackCreatedQueue() {
        return QueueBuilder.durable(TRACK_CREATED_QUEUE).build();
    }

    @Bean
    public Queue trackUpdatedQueue() {
        return QueueBuilder.durable(TRACK_UPDATED_QUEUE).build();
    }

    @Bean
    public Queue trackDeletedQueue() {
        return QueueBuilder.durable(TRACK_DELETED_QUEUE).build();
    }

    // Track Bindings
    @Bean
    public Binding trackCreatedBinding(Queue trackCreatedQueue, TopicExchange eventsExchange) {
        return BindingBuilder.bind(trackCreatedQueue).to(eventsExchange).with(TRACK_CREATED_ROUTING_KEY);
    }

    @Bean
    public Binding trackUpdatedBinding(Queue trackUpdatedQueue, TopicExchange eventsExchange) {
        return BindingBuilder.bind(trackUpdatedQueue).to(eventsExchange).with(TRACK_UPDATED_ROUTING_KEY);
    }

    @Bean
    public Binding trackDeletedBinding(Queue trackDeletedQueue, TopicExchange eventsExchange) {
        return BindingBuilder.bind(trackDeletedQueue).to(eventsExchange).with(TRACK_DELETED_ROUTING_KEY);
    }

    // Webhook Queues
    @Bean
    public Queue webhookCreatedQueue() {
        return QueueBuilder.durable(WEBHOOK_CREATED_QUEUE).build();
    }

    @Bean
    public Queue webhookUpdatedQueue() {
        return QueueBuilder.durable(WEBHOOK_UPDATED_QUEUE).build();
    }

    @Bean
    public Queue webhookDeletedQueue() {
        return QueueBuilder.durable(WEBHOOK_DELETED_QUEUE).build();
    }

    @Bean
    public Queue webhookTriggeredQueue() {
        return QueueBuilder.durable(WEBHOOK_TRIGGERED_QUEUE).build();
    }

    // Webhook Bindings
    @Bean
    public Binding webhookCreatedBinding(Queue webhookCreatedQueue, TopicExchange eventsExchange) {
        return BindingBuilder.bind(webhookCreatedQueue).to(eventsExchange).with(WEBHOOK_CREATED_ROUTING_KEY);
    }

    @Bean
    public Binding webhookUpdatedBinding(Queue webhookUpdatedQueue, TopicExchange eventsExchange) {
        return BindingBuilder.bind(webhookUpdatedQueue).to(eventsExchange).with(WEBHOOK_UPDATED_ROUTING_KEY);
    }

    @Bean
    public Binding webhookDeletedBinding(Queue webhookDeletedQueue, TopicExchange eventsExchange) {
        return BindingBuilder.bind(webhookDeletedQueue).to(eventsExchange).with(WEBHOOK_DELETED_ROUTING_KEY);
    }

    @Bean
    public Binding webhookTriggeredBinding(Queue webhookTriggeredQueue, TopicExchange eventsExchange) {
        return BindingBuilder.bind(webhookTriggeredQueue).to(eventsExchange).with(WEBHOOK_TRIGGERED_ROUTING_KEY);
    }
}
