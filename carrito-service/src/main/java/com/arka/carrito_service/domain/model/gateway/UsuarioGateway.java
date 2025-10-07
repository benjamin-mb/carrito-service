package com.arka.carrito_service.domain.model.gateway;

import com.arka.carrito_service.infrastructure.adapters.entity.UsuarioEntity;

import java.util.Optional;

public interface UsuarioGateway {
    Optional<UsuarioEntity> findById(Integer id);
    Boolean existsById(Integer id);
}
