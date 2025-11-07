package com.arka.carrito_service.infrastructure.adapters.repository;

import com.arka.carrito_service.infrastructure.adapters.entity.ProductosEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ProductoJpaRepository extends JpaRepository<ProductosEntity,Integer> {
}

