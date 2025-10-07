package com.arka.carrito_service.domain.exception;

public class CarritoActivoExistenteException extends RuntimeException {
    public CarritoActivoExistenteException(String message) {
        super(message);
    }
}
