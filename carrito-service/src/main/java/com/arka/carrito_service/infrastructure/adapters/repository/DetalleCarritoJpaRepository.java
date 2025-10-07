package com.arka.carrito_service.infrastructure.adapters.repository;

import com.arka.carrito_service.infrastructure.adapters.entity.DetalleCarritoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DetalleCarritoJpaRepository extends JpaRepository<DetalleCarritoEntity,Integer> {
    // Buscar todos los detalles por ID de carrito
    List<DetalleCarritoEntity> findByCarrito_IdCarrito(Integer idCarrito);

    // Buscar un detalle específico por ID de carrito e ID de producto
    Optional<DetalleCarritoEntity> findByCarrito_IdCarritoAndIdProducto(Integer idCarrito, Integer idProducto);

    // Eliminar todos los detalles asociados a un carrito
    void deleteByCarrito_IdCarrito(Integer idCarrito);

    // Verificar si existe un detalle con ese carrito y producto
    boolean existsByCarrito_IdCarritoAndIdProducto(Integer idCarrito, Integer idProducto);
}
