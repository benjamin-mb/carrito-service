package com.arka.carrito_service.carrito_service.domain.model;

import jakarta.validation.constraints.Email;

public class Usuario {

    private Integer id;
    private String nombre;
    private String email;
    private String password;
    private UserType tipo;
}
