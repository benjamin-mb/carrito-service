package com.arka.carrito_service.domain.model;

import lombok.Data;

@Data
public class Usuario {

    private Integer id;
    private String nombre;
    private String email;
    private String password;
    private UserType tipo;

    public Usuario(Integer id, String nombre, String email) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.tipo = UserType.cliente;
    }
}
