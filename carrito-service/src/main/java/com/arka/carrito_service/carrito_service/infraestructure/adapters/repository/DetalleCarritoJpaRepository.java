package com.arka.carrito_service.carrito_service.infraestructure.adapters.repository;

import com.arka.carrito_service.carrito_service.infraestructure.adapters.entity.DetalleCarritoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DetalleCarritoJpaRepository extends JpaRepository<DetalleCarritoEntity,Integer> {
    List<DetalleCarritoEntity> findByCarrito_IdCarrito(Integer idCarrito);

    Optional<DetalleCarritoEntity> findByCarrito_IdCarritoAndIdProducto(Integer idCarrito, Integer idProducto);

    void deleteByCarrito_IdCarrito(Integer idCarrito);

    boolean existsByCarrito_IdCarritoAndIdProducto(Integer idCarrito, Integer idProducto);
}
