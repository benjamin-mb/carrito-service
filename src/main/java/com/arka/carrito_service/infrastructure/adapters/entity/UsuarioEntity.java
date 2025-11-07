package com.arka.carrito_service.infrastructure.adapters.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Entity
@Table(name = "usuarios")
public class UsuarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, unique = true, length = 50)
    @Email(message = "please provide a valid email")
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('cliente','administrador')", nullable = true)
    private UserTypeEntity tipo;

    public UsuarioEntity(Integer id, String nombre, String email) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.tipo=UserTypeEntity.cliente;
    }
}
