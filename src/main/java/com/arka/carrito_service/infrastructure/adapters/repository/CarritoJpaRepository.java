package com.arka.carrito_service.infrastructure.adapters.repository;

import com.arka.carrito_service.infrastructure.adapters.entity.CarritoEntity;
import com.arka.carrito_service.infrastructure.adapters.entity.EstadoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public interface CarritoJpaRepository extends JpaRepository<CarritoEntity,Integer> {
    CarritoEntity save(CarritoEntity carrito);
    Optional<CarritoEntity> findById(Integer idCarrito);
    Optional<CarritoEntity> findByIdUsuarioAndEstado(Integer idUsuario, EstadoEntity estado);
    void deleteById(Integer idCarrito);
    boolean existsByIdUsuarioAndEstado(Integer idUsuario,EstadoEntity estado);
    List<CarritoEntity>findByEstadoAndCreadoBefore(EstadoEntity estado, LocalDateTime fecha);
}
