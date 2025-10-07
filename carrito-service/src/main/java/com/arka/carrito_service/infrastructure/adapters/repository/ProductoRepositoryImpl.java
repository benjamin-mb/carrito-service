package com.arka.carrito_service.infrastructure.adapters.repository;

import com.arka.carrito_service.domain.model.Producto;
import com.arka.carrito_service.domain.model.gateway.ProductoGateway;
import com.arka.carrito_service.infrastructure.adapters.mapper.ProductoMapper;
import jakarta.ws.rs.ext.ParamConverter;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class ProductoRepositoryImpl implements ProductoGateway {

    private final ProductoMapper productoMapper;
    private final ProductoJpaRepository productoJpaRepository;

    public ProductoRepositoryImpl(@Lazy ProductoMapper productoMapper, ProductoJpaRepository productoJpaRepository) {
        this.productoMapper = productoMapper;
        this.productoJpaRepository = productoJpaRepository;
    }

    @Override
    public Boolean existsById(Integer id) {
        return productoJpaRepository.existsById(id);
    }

    @Override
    public Optional<Producto> findById(Integer id) {

        return productoJpaRepository.findById(id)
                .map(productoMapper::toModel);
    }
}
