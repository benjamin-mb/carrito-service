package com.arka.carrito_service.infrastructure.adapters.exceptions;

public class CarritoNoEncontradoException extends RuntimeException {
    public CarritoNoEncontradoException(String message) {
        super(message);
    }
}
