package com.erval.argos.report.bootstrap.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Declares RabbitMQ infrastructure beans for the report service.
 */
@Configuration
public class ReportRabbitConfig {
    /**
     * JSON converter for message payloads.
     *
     * @return Jackson message converter
     */
    @Bean
    JacksonJsonMessageConverter jacksonConverter() {
        return new JacksonJsonMessageConverter();
    }

    /**
     * Rabbit template configured with the JSON converter.
     *
     * @param cf connection factory
     * @param conv message converter
     * @return configured rabbit template
     */
    @Bean
    RabbitTemplate rabbitTemplate(ConnectionFactory cf, JacksonJsonMessageConverter conv) {
        RabbitTemplate t = new RabbitTemplate(cf);
        t.setMessageConverter(conv);
        return t;
    }

    /**
     * Listener container factory with JSON conversion.
     *
     * @param connFactory connection factory
     * @param converter message converter
     * @return listener container factory
     */
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connFactory,
            JacksonJsonMessageConverter converter) {
        var factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connFactory);
        factory.setMessageConverter(converter);
        return factory;
    }

    /**
     * Exchange for Argos events.
     *
     * @return direct exchange
     */
    @Bean
    DirectExchange argosEventsExchange() {
        return new DirectExchange("argos.events");
    }

    /**
     * Queue for report requested events.
     *
     * @return durable queue
     */
    @Bean
    Queue reportRequestedQueue() {
        return new Queue("report.requested.v1", true);
    }

    /**
     * Binds the requested queue to the events exchange.
     *
     * @param reportRequestedQueue queue bean
     * @param argosEventsExchange exchange bean
     * @return binding
     */
    @Bean
    Binding bindReportRequested(Queue reportRequestedQueue, DirectExchange argosEventsExchange) {
        return BindingBuilder.bind(reportRequestedQueue)
                .to(argosEventsExchange)
                .with("report.requested.v1");
    }

}
