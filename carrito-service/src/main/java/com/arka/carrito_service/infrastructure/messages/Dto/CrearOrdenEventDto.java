package com.arka.carrito_service.infrastructure.messages.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CrearOrdenEventDto {
    private Integer idUsuario;
    private Integer montoTotal;
    private LocalDateTime fechaCreacion;
    private Integer idCarrito;
}
