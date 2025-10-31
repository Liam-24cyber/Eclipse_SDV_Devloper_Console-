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

package com.tsystems.dco.scenario.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String SDV_EVENTS_EXCHANGE = "sdv.events";
    public static final String SCENARIO_EVENTS_QUEUE = "scenario.events";
    public static final String SCENARIO_CREATED_ROUTING_KEY = "scenario.created";
    public static final String SCENARIO_UPDATED_ROUTING_KEY = "scenario.updated";
    public static final String SCENARIO_DELETED_ROUTING_KEY = "scenario.deleted";

    @Bean
    public TopicExchange sdvEventsExchange() {
        return new TopicExchange(SDV_EVENTS_EXCHANGE, true, false);
    }

    @Bean
    public Queue scenarioEventsQueue() {
        return new Queue(SCENARIO_EVENTS_QUEUE, true);
    }

    @Bean
    public Binding scenarioEventsBinding() {
        return BindingBuilder
                .bind(scenarioEventsQueue())
                .to(sdvEventsExchange())
                .with("scenario.*");
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}
