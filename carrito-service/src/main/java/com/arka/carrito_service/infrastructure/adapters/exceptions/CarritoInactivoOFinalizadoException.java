package com.arka.carrito_service.infrastructure.adapters.exceptions;

public class CarritoInactivoOFinalizadoException extends RuntimeException {
    public CarritoInactivoOFinalizadoException(String message) {
        super(message);
    }
}
