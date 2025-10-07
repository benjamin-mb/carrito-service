package com.arka.carrito_service.infrastructure.adapters.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "detalle_carrito")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleCarritoEntity {

    @Id
    @Column(name = "id_detalle_carrito")
    private Integer idDetalleCarrito;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_carrito", nullable = false)
    private CarritoEntity carrito;

    @Column(name = "id_producto")
    private Integer idProducto;

    private Integer cantidad;

    private Integer precioUnitario;

    private Integer subtotal;



    public Integer calcularSubtotal(){
        return this.cantidad*this.precioUnitario;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
        this.subtotal = calcularSubtotal();
    }

    public DetalleCarritoEntity(CarritoEntity carrito, Integer idProducto, Integer cantidad, Integer precioUnitario, Integer subtotal) {
        this.carrito = carrito;
        this.idProducto = idProducto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = calcularSubtotal();
    }
}
