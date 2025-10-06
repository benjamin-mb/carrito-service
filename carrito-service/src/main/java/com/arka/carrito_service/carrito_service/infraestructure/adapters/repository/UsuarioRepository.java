package com.arka.carrito_service.carrito_service.infraestructure.adapters.repository;

import com.arka.carrito_service.carrito_service.infraestructure.adapters.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<UsuarioEntity,Integer> {
    boolean existsById(Integer id);
}
