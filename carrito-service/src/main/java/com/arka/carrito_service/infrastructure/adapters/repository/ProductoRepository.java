package com.arka.carrito_service.infrastructure.adapters.repository;

import com.arka.carrito_service.infrastructure.adapters.entity.ProductosEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<ProductosEntity,Integer> {

    boolean existsById(Integer id);
    Optional<ProductosEntity> findById(Integer id);

}

