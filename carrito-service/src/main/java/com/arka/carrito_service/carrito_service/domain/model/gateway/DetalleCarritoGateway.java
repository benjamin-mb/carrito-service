package com.arka.carrito_service.carrito_service.domain.model.gateway;

import com.arka.carrito_service.carrito_service.domain.model.DetalleCarrito;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface DetalleCarritoGateway {

    Mono<DetalleCarrito>save(DetalleCarrito detalleCarrito);
    Mono<DetalleCarrito>findById(Integer idDetalleCarrito);
    Mono<DetalleCarrito>findByIdCarritoAndIdDetalleCarrito(Integer idCarrito, Integer idDetalleCarrito);
    Mono<Void>deleteById(Integer idDetalleCarrito);
    Mono<Void>deleteAllByIdCarrito(Integer idCarrito);
    Mono<Boolean> existeProductoEnCarrito(Integer idCarrito, Integer idProducto);
    Flux<DetalleCarrito> saveAll(Flux<DetalleCarrito> detalles);
    Flux<DetalleCarrito> findByIdCarrito(Integer idCarrito);


}
