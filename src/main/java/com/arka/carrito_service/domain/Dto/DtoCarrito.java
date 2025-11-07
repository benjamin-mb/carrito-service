package com.arka.carrito_service.domain.Dto;

import com.arka.carrito_service.domain.model.DetalleCarrito;
import com.arka.carrito_service.domain.model.Estado;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DtoCarrito {
    private LocalDateTime creado;
    private Estado estado;
    private List<DetalleCarrito> detalles;
    private Integer montoTotal;
}
