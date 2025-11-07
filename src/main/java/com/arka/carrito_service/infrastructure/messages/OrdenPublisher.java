package com.arka.carrito_service.infrastructure.messages;

import com.arka.carrito_service.infrastructure.config.RabbitMQConfig;
import com.arka.carrito_service.infrastructure.Dto.CrearOrdenEventDto;
import com.arka.carrito_service.infrastructure.Dto.DetallesDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrdenPublisher {

    private static final Logger log= LoggerFactory.getLogger(OrdenPublisher.class);
    private final RabbitTemplate rabbitTemplate;

    public OrdenPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishOrderConfirmed(CrearOrdenEventDto event){
        try{
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.ORDERS_CONFIRMED_EXCHANGE,
                    RabbitMQConfig.ORDERS_CONFIRMED_ROUTING_KEY,
                    event
            );
            log.info("all sent "+event.getIdUsuario() + event.getMontoTotal());
        } catch (Exception e) {
            log.error("message failed sending to order ms");
            throw new RuntimeException(e);
        }
    }

    public void publishReduceStock(DetallesDto event){
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.ORDERS_EXCHANGE,
                    RabbitMQConfig.ORDER_CREATED_ROUTING_KEY,
                    event
            );
            log.info("product to reduce stock sent");
        } catch (Exception e){
            log.error("message filed sending catalogo ms");
            throw new RuntimeException(e);
        }
    }
}
