package com.arka.carrito_service.domain.model.gateway;

import com.arka.carrito_service.domain.model.Carrito;
import com.arka.carrito_service.domain.model.Estado;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public interface CarritoGateway {

    Mono<Carrito> save(Carrito carrito);
    Mono<Carrito> findById(Integer idCarrito);
    Mono<Carrito>findCarritoActivoByIdUsuario(Integer idUsuario);
    Mono<Void> deleteById(Integer idCarrito);
    Mono<Boolean>findCarritoActivo(Integer idUsuario);
    Flux<Carrito> findCarritosAbandonados(LocalDateTime fecha);


}
