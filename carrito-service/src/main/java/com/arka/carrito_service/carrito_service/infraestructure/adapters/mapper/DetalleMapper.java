package com.arka.carrito_service.carrito_service.infraestructure.adapters.mapper;

import com.arka.carrito_service.carrito_service.domain.model.DetalleCarrito;
import com.arka.carrito_service.carrito_service.infraestructure.adapters.entity.CarritoEntity;
import com.arka.carrito_service.carrito_service.infraestructure.adapters.entity.DetalleCarritoEntity;
import com.arka.carrito_service.carrito_service.infraestructure.adapters.entity.ProductosEntity;
import com.arka.carrito_service.carrito_service.infraestructure.adapters.repository.CarritoJpaRepository;
import com.arka.carrito_service.carrito_service.infraestructure.adapters.repository.ProductoRepository;

public class DetalleMapper {

    private final CarritoJpaRepository carritoJpaRepository;
    private final ProductoRepository productoRepository;

    public DetalleMapper(CarritoJpaRepository carritoJpaRepository, ProductoRepository productoRepository) {
        this.carritoJpaRepository = carritoJpaRepository;
        this.productoRepository = productoRepository;
    }

    public DetalleCarrito toDomain(DetalleCarritoEntity detalleCarritoEntity){
        DetalleCarrito detalleCarrito =new DetalleCarrito(
                detalleCarritoEntity.getCarritoEntity().getIdCarrito(),
                detalleCarritoEntity.getProductoEntity().getId(),
                detalleCarritoEntity.getCantidad(),
                detalleCarritoEntity.getPrecioUnitario(),
                detalleCarritoEntity.getSubtotal()
        );
        return detalleCarrito;
    }

    public DetalleCarritoEntity toEntity (DetalleCarrito detalleCarrito){
        DetalleCarritoEntity detalleCarritoEntity= new DetalleCarritoEntity();
        CarritoEntity carritoEntity= carritoJpaRepository.findById(detalleCarrito.getIdCarrito())
                .orElseThrow(()->new IllegalArgumentException("no car entity"));
        ProductosEntity productosEntity= productoRepository.findById(detalleCarrito.getIdProducto())
                        .orElseThrow(()->new IllegalArgumentException("no product found"));
        detalleCarritoEntity.setCarritoEntity(carritoEntity);
        detalleCarritoEntity.setProductoEntity(productosEntity);
        detalleCarritoEntity.setCantidad(detalleCarrito.getCantidad());
        detalleCarritoEntity.setPrecioUnitario(detalleCarrito.getPrecioUnitario());
        detalleCarritoEntity.setSubtotal(detalleCarritoEntity.getSubtotal());
        return detalleCarritoEntity;
    }
}
