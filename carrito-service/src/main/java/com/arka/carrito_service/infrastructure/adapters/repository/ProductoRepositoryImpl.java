package com.arka.carrito_service.infrastructure.adapters.repository;

import com.arka.carrito_service.domain.model.Producto;
import com.arka.carrito_service.domain.model.gateway.ProductoGateway;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Component
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
