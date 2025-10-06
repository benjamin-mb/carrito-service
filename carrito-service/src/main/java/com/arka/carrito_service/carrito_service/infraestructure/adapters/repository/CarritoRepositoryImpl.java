package com.arka.carrito_service.carrito_service.infraestructure.adapters.repository;

import com.arka.carrito_service.carrito_service.domain.model.Carrito;
import com.arka.carrito_service.carrito_service.domain.model.gateway.CarritoGateway;
import reactor.core.publisher.Mono;

public class CarritoRepositoryImpl implements CarritoGateway {
    @Override
    public Mono<Carrito> save(Carrito carrito) {
        return null;
    }

    @Override
    public Mono<Carrito> findById(Integer idCarrito) {
        return null;
    }

    @Override
    public Mono<Carrito> findCarritoActivoByIdUsuario(Integer idUsuario) {
        return null;
    }

    @Override
    public Mono<Carrito> deleteById(Integer idCarrito) {
        return null;
    }

    @Override
    public Mono<Boolean> findCarritoActivo(Integer idUsuario) {
        return null;
    }
}
