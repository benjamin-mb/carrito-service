package com.arka.carrito_service.domain.exception;

public class DetalleCarritoNoEncontradoException extends RuntimeException {
    public DetalleCarritoNoEncontradoException(String message) {
        super(message);
    }
}
