package com.arka.carrito_service.carrito_service.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class DetalleCarrito {

    private Integer idDdetalleCarrito;
    private Integer idCarrito;
    private Integer idProducto;
    private Integer cantidad;
    private Integer precioUnitario;
    private Integer subtotal = calcularSubtotal();


    public DetalleCarrito(Integer idCarrito, Integer idProducto, Integer cantidad, Integer precioUnitario, Integer subtotal) {
        this.idCarrito = idCarrito;
        this.idProducto = idProducto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = subtotal;
    }

    public Integer calcularSubtotal(){
        return this.cantidad*this.precioUnitario;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
        this.subtotal = calcularSubtotal();
    }
}
