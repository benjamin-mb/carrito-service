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
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}
