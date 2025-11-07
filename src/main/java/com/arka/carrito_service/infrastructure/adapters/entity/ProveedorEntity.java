package com.arka.carrito_service.infrastructure.adapters.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "proveedores")
public class ProveedorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_proveedor")
    private Integer id;

    @Column(length = 100, nullable = false,unique = true)
    private String nombre;

    @Column(length = 20,nullable = false)
    private String telefono;

    @Column(columnDefinition = "TEXT")
    private String caracteristicas;
}
