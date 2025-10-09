package com.arka.carrito_service.infrastructure.config;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    /// REDUCE STOCK EXCHANGE AND ROUTING KEY
    public static final String ORDERS_EXCHANGE = "orders.exchange";
    public static final String ORDER_CREATED_ROUTING_KEY = "order.created";

    /// CONFIRMED ORDER TO CREATE ORDER EXCHANGE AND ROUTING KEY
    public static final String ORDERS_CONFIRMED_EXCHANGE= "confirmed.exchange";
    public static  final String ORDERS_CONFIRMED_ROUTING_KEY="order.confirmed";

    /// RETRY FOR ERRORS
    private static final int MAX_RETRY_ATTEMPTS = 3;
    private static final long RETRY_INITIAL_INTERVAL = 2000L;
    private static final long RETRY_MAX_INTERVAL = 10000L;
    private static final double RETRY_MULTIPLIER = 2.0;

    @Bean
    public TopicExchange ordersExchange(){
        return new TopicExchange(ORDERS_EXCHANGE);
    }

    @Bean
    public TopicExchange confirmedExchange(){
        return new TopicExchange(ORDERS_CONFIRMED_EXCHANGE);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public org.springframework.retry.support.RetryTemplate retryTemplate() {
        org.springframework.retry.support.RetryTemplate retryTemplate =
                new org.springframework.retry.support.RetryTemplate();
        org.springframework.retry.backoff.ExponentialBackOffPolicy backOffPolicy =
                new org.springframework.retry.backoff.ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(RETRY_INITIAL_INTERVAL);
        backOffPolicy.setMaxInterval(RETRY_MAX_INTERVAL);
        backOffPolicy.setMultiplier(RETRY_MULTIPLIER);
        retryTemplate.setBackOffPolicy(backOffPolicy);

        org.springframework.retry.policy.SimpleRetryPolicy retryPolicy =
                new org.springframework.retry.policy.SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(MAX_RETRY_ATTEMPTS);
        retryTemplate.setRetryPolicy(retryPolicy);

        return retryTemplate;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        template.setRetryTemplate(retryTemplate());
        return template;
    }

}
