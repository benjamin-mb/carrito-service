package com.arka.carrito_service.carrito_service.domain.model.gateway;

import com.arka.carrito_service.carrito_service.domain.model.Carrito;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CarritoGateway {

    Mono<Carrito> save(Carrito carrito);
    Mono<Carrito>findById(Integer idCarrito);
    Mono<Carrito>findCarritoActivoByIdUsuario(Integer idUsuario);
    Mono<Carrito>deleteById(Integer idCarrito);
    Mono<Boolean>findCarritoActivo(Integer idUsuario);
    Flux<Carrito>findAllByIdUsuario(Integer idUsuario);

}
