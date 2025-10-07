package com.arka.carrito_service.infrastructure.adapters.mapper;

import com.arka.carrito_service.domain.model.DetalleCarrito;
import com.arka.carrito_service.infrastructure.adapters.entity.CarritoEntity;
import com.arka.carrito_service.infrastructure.adapters.entity.DetalleCarritoEntity;
import com.arka.carrito_service.infrastructure.adapters.entity.ProductosEntity;
import com.arka.carrito_service.infrastructure.adapters.repository.CarritoJpaRepository;

import org.springframework.stereotype.Component;

@Component
public class DetalleMapper {

    public DetalleCarrito toDomain(DetalleCarritoEntity entity) {
        if (entity == null) return null;

        return new DetalleCarrito(
                entity.getId(),
                entity.getCarrito() != null ? entity.getCarrito().getIdCarrito() : null,
                entity.getIdProducto(),
                entity.getCantidad(),
                entity.getPrecioUnitario(),
                entity.getSubtotal()
        );
    }

    public DetalleCarritoEntity toEntity(DetalleCarrito detalle, CarritoEntity carritoRef) {
        if (detalle == null) return null;

        DetalleCarritoEntity entity = new DetalleCarritoEntity();
        entity.setId(detalle.getIdDdetalleCarrito());
        entity.setCarrito(carritoRef);
        entity.setIdProducto(detalle.getIdProducto());
        entity.setCantidad(detalle.getCantidad());
        entity.setPrecioUnitario(detalle.getPrecioUnitario());
        entity.setSubtotal(detalle.getSubtotal());
        return entity;
    }

}
