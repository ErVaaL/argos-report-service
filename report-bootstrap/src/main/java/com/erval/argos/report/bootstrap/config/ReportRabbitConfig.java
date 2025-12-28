package com.erval.argos.report.bootstrap.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ReportRabbitConfig {
    @Bean
    JacksonJsonMessageConverter jacksonConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    RabbitTemplate rabbitTemplate(ConnectionFactory cf, JacksonJsonMessageConverter conv) {
        RabbitTemplate t = new RabbitTemplate(cf);
        t.setMessageConverter(conv);
        return t;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connFactory,
            JacksonJsonMessageConverter converter) {
        var factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connFactory);
        factory.setMessageConverter(converter);
        return factory;
    }

    @Bean
    DirectExchange argosEventsExchange() {
        return new DirectExchange("argos.events");
    }

    @Bean
    Queue reportRequestedQueue() {
        return new Queue("report.requested.v1", true);
    }

    @Bean
    Binding bindReportRequested(Queue reportRequestedQueue, DirectExchange argosEventsExchange) {
        return BindingBuilder.bind(reportRequestedQueue)
                .to(argosEventsExchange)
                .with("report.requested.v1");
    }

}
