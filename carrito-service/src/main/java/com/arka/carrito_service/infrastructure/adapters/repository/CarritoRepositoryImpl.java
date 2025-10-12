package com.arka.carrito_service.infrastructure.adapters.repository;

import com.arka.carrito_service.domain.model.Carrito;
import com.arka.carrito_service.domain.model.Estado;
import com.arka.carrito_service.domain.model.gateway.CarritoGateway;
import com.arka.carrito_service.infrastructure.adapters.entity.CarritoEntity;
import com.arka.carrito_service.infrastructure.adapters.entity.EstadoEntity;
import com.arka.carrito_service.infrastructure.adapters.exceptions.CarritoInactivoOFinalizadoException;
import com.arka.carrito_service.infrastructure.adapters.exceptions.CarritoNoEncontradoException;
import com.arka.carrito_service.infrastructure.adapters.mapper.CarritoMapper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;
import java.util.Optional;

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
        return Mono.fromCallable(()->
            carritoJpaRepository.findById(idCarrito)
                    .map(carritoMapper::toDomain)
                    .orElse(null)
        ).subscribeOn(Schedulers.boundedElastic())
                .onErrorMap(e->new CarritoNoEncontradoException("car not found"));

    }

    @Override
    public Mono<Carrito> findCarritoActivoByIdUsuario(Integer idUsuario) {
        return Mono.fromCallable(()->
        carritoJpaRepository.findByIdUsuarioAndEstado(idUsuario, EstadoEntity.abierto)
                .map(carritoMapper::toDomain)
                .orElse(null)
                ).subscribeOn(Schedulers.boundedElastic())
                .onErrorMap(e->new CarritoNoEncontradoException("car not found for user or car not active for user"));
    }

    @Override
    public Mono<Void> deleteById(Integer idCarrito) {
        return Mono.fromRunnable(()->
                carritoJpaRepository.deleteById(idCarrito))
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }

    @Override
    public Flux<Carrito> findCarritosAbandonados(LocalDateTime fecha) {
        return Mono.fromCallable(()->
                carritoJpaRepository.findByEstadoAndCreadoBefore(
                        EstadoEntity.abierto,
                        fecha)).flatMapMany(Flux::fromIterable)
                .map(carritoMapper::toDomain)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Boolean> findCarritoActivo(Integer idUsuario) {
        return Mono.fromCallable(()->
                carritoJpaRepository.existsByIdUsuarioAndEstado(idUsuario,EstadoEntity.abierto)
        ).subscribeOn(Schedulers.boundedElastic())
                .onErrorMap(e->new CarritoInactivoOFinalizadoException("Car innactive or finished"));
    }

}
