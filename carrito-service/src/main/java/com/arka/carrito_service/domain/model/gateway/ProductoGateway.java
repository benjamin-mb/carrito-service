package com.arka.carrito_service.domain.model.gateway;

import com.arka.carrito_service.domain.model.Producto;

import java.util.Optional;

public interface ProductoGateway {
    Boolean existsById(Integer id);
    Optional<Producto> findById(Integer id);
}
