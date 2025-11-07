package com.arka.carrito_service.infrastructure.Dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class CarritoAbandonadoDto {

    private String usuarioEmail;
    private List<DetalleWeebhookDto> detalles;
    private Integer total;

}
