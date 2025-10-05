package com.arka.carrito_service.carrito_service.domain.exception;

public class CarritoVacioException extends RuntimeException {
    public CarritoVacioException(String message) {
        super(message);
    }
}
