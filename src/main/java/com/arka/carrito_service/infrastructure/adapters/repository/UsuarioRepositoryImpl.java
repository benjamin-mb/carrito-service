package com.arka.carrito_service.infrastructure.adapters.repository;

import com.arka.carrito_service.domain.model.Usuario;
import com.arka.carrito_service.domain.model.gateway.UsuarioGateway;
import com.arka.carrito_service.infrastructure.adapters.mapper.UsuarioMapper;

import java.util.Optional;

public class UsuarioRepositoryImpl implements UsuarioGateway {

    private final UsuarioJpaRepository usuarioJpaRepository;
    private final UsuarioMapper usuarioMapper;

    public UsuarioRepositoryImpl(UsuarioJpaRepository usuarioJpaRepository, UsuarioMapper usuarioMapper) {
        this.usuarioJpaRepository = usuarioJpaRepository;
        this.usuarioMapper = usuarioMapper;
    }

    @Override
    public Optional<Usuario> findById(Integer id) {
        return usuarioJpaRepository.findById(id)
                .map(usuarioMapper::toDomain);
    }

    @Override
    public Boolean existsById(Integer id) {
        return usuarioJpaRepository.existsById(id);
    }
}
