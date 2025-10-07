package com.arka.carrito_service.infrastructure.adapters.repository;

import com.arka.carrito_service.domain.model.gateway.UsuarioGateway;
import com.arka.carrito_service.infrastructure.adapters.entity.UsuarioEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Component
public class UsuarioRepositoryImpl implements UsuarioGateway {
    @Override
    public Boolean existsById(Integer id) {
        return null;
    }

    @Override
    public Optional<UsuarioEntity> findById(Integer id) {
        return Optional.empty();
    }
}
