package com.arka.carrito_service.infrastructure.messages;

import com.arka.carrito_service.infrastructure.config.RabbitMQConfig;
import com.arka.carrito_service.infrastructure.messages.Dto.CrearOrdenEventDto;
import com.arka.carrito_service.infrastructure.messages.Dto.DetalleReduceStockDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrderPublisher {

    private static final Logger log= LoggerFactory.getLogger(OrderPublisher.class);
    private final RabbitTemplate rabbitTemplate;

    public OrderPublisher(RabbitTemplate rabbitTemplate) {
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

    public void publishReduceStock(DetalleReduceStockDto event){
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.ORDERS_EXCHANGE,
                    RabbitMQConfig.ORDER_CREATED_ROUTING_KEY,
                    event
            );
            log.info("product to reduce stock sent: " + event.getIdProducto(), event.getCantidad());
        } catch (Exception e){
            log.error("message filed sending catalogo ms");
            throw new RuntimeException(e);
        }
    }
}
