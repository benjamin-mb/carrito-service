package com.arka.carrito_service.carrito_service.infraestructure.adapters.repository;

import com.arka.carrito_service.carrito_service.domain.model.Producto;
import com.arka.carrito_service.carrito_service.infraestructure.adapters.entity.ProductosEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductoRepository extends JpaRepository<ProductosEntity,Integer> {

    boolean existsById(Integer id);
    Optional<ProductosEntity> findById(Integer id);

}

