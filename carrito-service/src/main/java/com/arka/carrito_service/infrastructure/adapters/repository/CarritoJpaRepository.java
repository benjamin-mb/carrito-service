package com.arka.carrito_service.infrastructure.adapters.repository;

import com.arka.carrito_service.infrastructure.adapters.entity.CarritoEntity;
import com.arka.carrito_service.infrastructure.adapters.entity.EstadoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CarritoJpaRepository extends JpaRepository<CarritoEntity,Integer> {
    CarritoEntity save(CarritoEntity carrito);
    Optional<CarritoEntity> findById(Integer idCarrito);
    CarritoEntity findCarritoActivoByIdUsuario(Integer idUsuario);
    void deleteById(Integer idCarrito);
    boolean existsByIdUsuarioAndEstado(Integer idUsuario, EstadoEntity estado);
}
