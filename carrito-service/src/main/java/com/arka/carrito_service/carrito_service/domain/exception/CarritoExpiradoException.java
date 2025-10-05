package com.arka.carrito_service.carrito_service.domain.exception;

public class CarritoExpiradoException extends RuntimeException {
    public CarritoExpiradoException(String message) {
        super(message);
    }
}
