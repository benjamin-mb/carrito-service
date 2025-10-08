package com.arka.carrito_service.infrastructure.messages.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetalleReduceStockDto {
    private Integer idProducto;
    private Integer cantidad;
}
