package com.arka.carrito_service.infrastructure.adapters.exceptions;

public class DetalleCarritoNoEncontradoException extends RuntimeException {
  public DetalleCarritoNoEncontradoException(String message) {
    super(message);
  }
}
