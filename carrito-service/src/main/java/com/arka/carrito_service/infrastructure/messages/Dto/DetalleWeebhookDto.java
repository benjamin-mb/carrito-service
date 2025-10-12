package com.arka.carrito_service.infrastructure.messages.Dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DetalleWeebhookDto {

    private String nombreProducto;
    private Integer cantidad;
    private Integer subtotal;
}
