package com.arka.carrito_service.domain.model.gateway;

import com.arka.carrito_service.domain.model.DetalleCarrito;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface DetalleCarritoGateway {

    Mono<DetalleCarrito>save(DetalleCarrito detalleCarrito);
    Mono<DetalleCarrito>findById(Integer idDetalleCarrito);
    Mono<DetalleCarrito>findByIdCarritoAndIdProducto(Integer idCarrito, Integer idProdcuto);
    Mono<Void>deleteById(Integer idDetalleCarrito);



}
