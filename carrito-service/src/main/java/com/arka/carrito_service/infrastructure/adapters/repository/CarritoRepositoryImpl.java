package com.arka.carrito_service.infrastructure.adapters.repository;

import com.arka.carrito_service.domain.model.Carrito;
import com.arka.carrito_service.domain.model.gateway.CarritoGateway;
import com.arka.carrito_service.infrastructure.adapters.entity.CarritoEntity;
import com.arka.carrito_service.infrastructure.adapters.mapper.CarritoMapper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
public class CarritoRepositoryImpl implements CarritoGateway {

    private final CarritoJpaRepository carritoJpaRepository;
    private final CarritoMapper carritoMapper;

    public CarritoRepositoryImpl(CarritoJpaRepository carritoJpaRepository, CarritoMapper carritoMapper) {
        this.carritoJpaRepository = carritoJpaRepository;
        this.carritoMapper = carritoMapper;
    }

    @Override
    public Mono<Carrito> save(Carrito carrito) {
        return Mono.fromCallable(() -> {
                    CarritoEntity entity = carritoMapper.toEntity(carrito);
                    CarritoEntity saved = carritoJpaRepository.save(entity);
                    return carritoMapper.toDomain(saved);
                })
                .subscribeOn(Schedulers.boundedElastic());
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
