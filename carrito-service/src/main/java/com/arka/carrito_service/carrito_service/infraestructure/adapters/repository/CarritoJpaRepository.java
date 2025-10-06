package com.arka.carrito_service.carrito_service.infraestructure.adapters.repository;

import com.arka.carrito_service.carrito_service.infraestructure.adapters.entity.CarritoEntity;
import com.arka.carrito_service.carrito_service.infraestructure.adapters.entity.EstadoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import reactor.core.publisher.Mono;

import java.util.Optional;

public interface CarritoJpaRepository extends JpaRepository<CarritoEntity,Integer> {
    Mono<CarritoEntity> save(CarritoEntity carrito);
    Optional<CarritoEntity> findById(Integer idCarrito);
    Mono<CarritoEntity>findCarritoActivoByIdUsuario(Integer idUsuario);
    void deleteById(Integer idCarrito);
    boolean existsByIdUsuarioAndEstado(Integer idUsuario, EstadoEntity estado);
}
