package com.arka.carrito_service.domain.model.gateway;

import com.arka.carrito_service.domain.model.Carrito;
import reactor.core.publisher.Mono;

public interface NotificacionGateway {
    Mono<Void>sendNotiOfCarritoAbandonado(Carrito carrito);
}
