package com.arka.carrito_service.infrastructure.adapters.repository;

import com.arka.carrito_service.infrastructure.adapters.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;
@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioEntity,Integer> {
    boolean existsById(Integer id);
    Optional<UsuarioEntity>findById(Integer id);
}
