package com.arka.carrito_service.domain.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Carrito {
    private Integer idCarrito;
    private Integer idUsuario;
    private LocalDateTime creado;
    private Estado estado;
    private LocalDateTime expirado;
    private List<DetalleCarrito> detalles;

    public Carrito(Integer idUsuario, LocalDateTime creado, Estado estado, LocalDateTime expirado,List<DetalleCarrito> detalles) {
        this.idUsuario = idUsuario;
        this.creado = creado;
        this.estado = estado;
        this.expirado = expirado;
        this.detalles = new ArrayList<>();
    }
}
