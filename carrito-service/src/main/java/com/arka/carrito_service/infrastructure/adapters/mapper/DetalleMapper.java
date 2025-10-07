package com.arka.carrito_service.infrastructure.adapters.mapper;

import com.arka.carrito_service.domain.model.DetalleCarrito;
import com.arka.carrito_service.infrastructure.adapters.entity.CarritoEntity;
import com.arka.carrito_service.infrastructure.adapters.entity.DetalleCarritoEntity;
import com.arka.carrito_service.infrastructure.adapters.entity.ProductosEntity;
import com.arka.carrito_service.infrastructure.adapters.repository.CarritoJpaRepository;
import com.arka.carrito_service.infrastructure.adapters.repository.ProductoRepository;
import org.springframework.stereotype.Component;

@Component
public class DetalleMapper {

     private final CarritoJpaRepository carritoJpaRepository;

    public DetalleMapper(CarritoJpaRepository carritoJpaRepository) {
        this.carritoJpaRepository = carritoJpaRepository;
    }

    public DetalleCarrito toDomain(DetalleCarritoEntity entity) {
        if (entity == null) return null;

        return new DetalleCarrito(
                entity.getIdDetalleCarrito(),
                entity.getCarrito().getIdCarrito(), // ← Solo el ID
                entity.getIdProducto(), // ← Ya es un Integer
                entity.getCantidad(),
                entity.getPrecioUnitario(),
                entity.getSubtotal()
        );
    }

    public DetalleCarritoEntity toEntity(DetalleCarrito detalle) {
        if (detalle == null) return null;


        CarritoEntity carritoRef = carritoJpaRepository.findById(detalle.getIdCarrito())
                .orElseThrow(()->new IllegalArgumentException("car not found"));


        DetalleCarritoEntity entity = new DetalleCarritoEntity();
        entity.setIdDetalleCarrito(detalle.getIdDdetalleCarrito());
        entity.setCarrito(carritoRef); // ← Referencia ligera
        entity.setIdProducto(detalle.getIdProducto()); // ← Directo
        entity.setCantidad(detalle.getCantidad());
        entity.setPrecioUnitario(detalle.getPrecioUnitario());
        entity.setSubtotal(detalle.getSubtotal()); // ← Corregido

        return entity;
    }


}
