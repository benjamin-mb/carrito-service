package com.arka.carrito_service.carrito_service.infraestructure.adapters.entity;

import com.arka.carrito_service.carrito_service.domain.model.DetalleCarrito;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "carritos")
@NoArgsConstructor
@Data
public class CarritoEntity {

    @Id
    @Column(name = "id_carrito")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idCarrito;

    @Column(name = "id_usuario")
    private UsuarioEntity usuario;

    @Column(name = "creado_en")
    @DateTimeFormat
    private LocalDateTime creado;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    private EstadoEntity estado;

    @Column(name = "expirado")
    private LocalDateTime expirado;

    @OneToMany(mappedBy = "carrito", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DetalleCarrito> detalles;

    public CarritoEntity(UsuarioEntity usuario, LocalDateTime creado, EstadoEntity estado, LocalDateTime expirado, List<DetalleCarrito> detalles) {
        this.usuario = usuario;
        this.creado = creado;
        this.estado = estado;
        this.expirado = expirado;
        this.detalles = detalles;
    }
}
