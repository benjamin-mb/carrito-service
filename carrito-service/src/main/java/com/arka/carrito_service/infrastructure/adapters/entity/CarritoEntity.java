package com.arka.carrito_service.infrastructure.adapters.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "carritos")
@NoArgsConstructor
@Setter
@Getter
public class CarritoEntity {

    @Id
    @Column(name = "id_carrito")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idCarrito;

    @Column(name = "id_usuario")
    private Integer idUsuario;

    @Column(name = "creado_en")
    @DateTimeFormat
    private LocalDateTime creado;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    private EstadoEntity estado;

    @Column(name = "expirado")
    private LocalDateTime expirado;

    @OneToMany(mappedBy = "carrito", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<DetalleCarritoEntity> detalles;

    public CarritoEntity(Integer idUsuario, LocalDateTime creado, EstadoEntity estado, LocalDateTime expirado, List<DetalleCarritoEntity> detalles) {
        this.idUsuario=idUsuario;
        this.creado = creado;
        this.estado = estado;
        this.expirado = expirado;
        this.detalles = detalles;
    }
}
