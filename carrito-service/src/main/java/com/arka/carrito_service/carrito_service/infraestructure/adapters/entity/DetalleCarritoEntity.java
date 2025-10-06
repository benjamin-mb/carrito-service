package com.arka.carrito_service.carrito_service.infraestructure.adapters.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "detalle_carrito")
@Data
@NoArgsConstructor
public class DetalleCarritoEntity {

    @Column(name = "id_detalle_carrito")
    private Integer idDetalleCarrito;

    @Column(name="id_carrito")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_carrito", nullable = false)
    private CarritoEntity carritoEntity;

    @Column(name = "id_producto")
    private ProductosEntity productoEntity;

    private Integer cantidad;

    private Integer precioUnitario;

    private Integer subtotal=calcularSubtotal();



    public Integer calcularSubtotal(){
        return this.cantidad*this.precioUnitario;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
        this.subtotal = calcularSubtotal();
    }

    public DetalleCarritoEntity(CarritoEntity carritoEntity, ProductosEntity productoEntity, Integer cantidad, Integer precioUnitario, Integer subtotal) {
        this.carritoEntity = carritoEntity;
        this.productoEntity = productoEntity;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = subtotal;
    }
}
