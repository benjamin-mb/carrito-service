package com.arka.carrito_service.infrastructure.adapters.repository;

import com.arka.carrito_service.domain.model.DetalleCarrito;
import com.arka.carrito_service.domain.model.gateway.DetalleCarritoGateway;
import com.arka.carrito_service.infrastructure.adapters.entity.CarritoEntity;
import com.arka.carrito_service.infrastructure.adapters.entity.DetalleCarritoEntity;
import com.arka.carrito_service.infrastructure.adapters.exceptions.CarritoNoEncontradoException;
import com.arka.carrito_service.infrastructure.adapters.exceptions.DetalleCarritoNoEncontradoException;
import com.arka.carrito_service.infrastructure.adapters.mapper.DetalleMapper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Repository
public class DetalleCarritoRepositoryImpl implements DetalleCarritoGateway {

    private final DetalleCarritoJpaRepository detalleRepository;
    private final DetalleMapper detalleMapper;

    public DetalleCarritoRepositoryImpl(
            DetalleCarritoJpaRepository detalleRepository,
            DetalleMapper detalleMapper
    ) {
        this.detalleRepository = detalleRepository;
        this.detalleMapper = detalleMapper;
    }

    @Override
    public Mono<DetalleCarrito> save(DetalleCarrito detalleCarrito) {
        return Mono.fromCallable(() -> {
                    // Crear referencia ligera al carrito (sin query)
                    CarritoEntity carritoRef = new CarritoEntity();
                    carritoRef.setIdCarrito(detalleCarrito.getIdCarrito());

                    DetalleCarritoEntity entity = detalleMapper.toEntity(detalleCarrito, carritoRef);
                    DetalleCarritoEntity saved = detalleRepository.save(entity);
                    return detalleMapper.toDomain(saved);
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<DetalleCarrito> findById(Integer idDetalleCarrito) {
        return Mono.fromCallable(() ->
                        detalleRepository.findById(idDetalleCarrito)
                                .map(detalleMapper::toDomain)
                                .orElse(null)
                )
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<DetalleCarrito> findByIdCarritoAndIdProducto(Integer idCarrito, Integer idProducto) {
        return Mono.fromCallable(() ->
                        detalleRepository.findByCarrito_IdCarritoAndIdProducto(idCarrito, idProducto)
                                .map(detalleMapper::toDomain)
                                .orElse(null)
                )
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Void> deleteById(Integer idDetalleCarrito) {
        return Mono.fromRunnable(() -> detalleRepository.deleteById(idDetalleCarrito))
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }
}


