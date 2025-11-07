package com.arka.carrito_service.infrastructure.adapters.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "productos")
@Data
@NoArgsConstructor
@Entity
public class ProductosEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Integer id;

    @Column(nullable = false, unique = true,length = 70)
    private String nombre;

    @Column(nullable = false)
    private Integer precio;

    @Column(nullable = false)
    private Integer stock;

    @Column(columnDefinition = "TEXT")
    private String caracteristicas;

    @Column(nullable = false)
    private String marca;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_proveedor",referencedColumnName = "id_proveedor")
    private ProveedorEntity proveedor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categoria",referencedColumnName = "id_categoria")
    private CategoriasEntity categoria;

    public ProductosEntity(String nombre, Integer precio, Integer stock, String caracteristicas, String marca, ProveedorEntity proveedor, CategoriasEntity categoria) {
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
        this.caracteristicas = caracteristicas;
        this.marca = marca;
        this.proveedor = proveedor;
        this.categoria = categoria;
    }
}
