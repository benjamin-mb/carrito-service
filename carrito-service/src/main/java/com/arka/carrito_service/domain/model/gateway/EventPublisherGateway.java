package com.arka.carrito_service.domain.model.gateway;

import com.arka.carrito_service.domain.model.Carrito;

public interface EventPublisherGateway {
    void publishOrderConfirmed(Carrito carrito);
    void publishReduceStock(Carrito carrito);
}
