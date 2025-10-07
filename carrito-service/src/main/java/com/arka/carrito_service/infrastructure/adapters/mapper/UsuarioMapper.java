package com.arka.carrito_service.infrastructure.adapters.mapper;

import com.arka.carrito_service.domain.model.Usuario;
import com.arka.carrito_service.infrastructure.adapters.entity.UsuarioEntity;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

    public Usuario toDomain(UsuarioEntity usuario){
        if (usuario == null)return null;
        Usuario domain=new Usuario(
               usuario.getId(),
                usuario.getNombre(),
                usuario.getEmail()
        );
        return domain;
    }

    public UsuarioEntity toEnity(Usuario usuario){
        if(usuario == null)return null;
        UsuarioEntity entity=new UsuarioEntity(
            usuario.getId(),
            usuario.getNombre(),
            usuario.getEmail()
                );
        return entity;
    }
}
