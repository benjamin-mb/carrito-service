package com.arka.carrito_service.carrito_service.infraestructure.adapters.repository;

import com.arka.carrito_service.carrito_service.domain.model.DetalleCarrito;
import com.arka.carrito_service.carrito_service.domain.model.gateway.DetalleCarritoGateway;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class DetalleCarritoRepositoryImpl implements DetalleCarritoGateway {
    @Override
    public Mono<DetalleCarrito> save(DetalleCarrito detalleCarrito) {
        return null;
    }

    @Override
    public Mono<DetalleCarrito> findById(Integer idDetalleCarrito) {
        return null;
    }

    @Override
    public Mono<DetalleCarrito> findByIdCarritoAndIdProducto(Integer idCarrito, Integer idProdcuto) {
        return null;
    }

    @Override
    public Mono<DetalleCarrito> findByIdCarritoAndIdDetalleCarrito(Integer idCarrito, Integer idDetalleCarrito) {
        return null;
    }

    @Override
    public Mono<Void> deleteById(Integer idDetalleCarrito) {
        return null;
    }

    @Override
    public Mono<Void> deleteAllByIdCarrito(Integer idCarrito) {
        return null;
    }

    @Override
    public Mono<Boolean> existeProductoEnCarrito(Integer idCarrito, Integer idProducto) {
        return null;
    }

    @Override
    public Flux<DetalleCarrito> saveAll(Flux<DetalleCarrito> detalles) {
        return null;
    }

    @Override
    public Flux<DetalleCarrito> findByIdCarrito(Integer idCarrito) {
        return null;
    }
}
