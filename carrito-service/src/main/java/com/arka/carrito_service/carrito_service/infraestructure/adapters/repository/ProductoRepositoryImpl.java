package com.arka.carrito_service.carrito_service.infraestructure.adapters.repository;

import com.arka.carrito_service.carrito_service.domain.model.Producto;
import com.arka.carrito_service.carrito_service.domain.model.gateway.ProductoGateway;

import java.util.Optional;

public class ProductoRepositoryImpl implements ProductoGateway {
    @Override
    public Boolean existsById(Integer id) {
        return null;
    }

    @Override
    public Optional<Producto> findById(Integer id) {
        return Optional.empty();
    }
}
