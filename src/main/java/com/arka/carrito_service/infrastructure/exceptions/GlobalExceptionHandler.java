package com.arka.carrito_service.infrastructure.exceptions;

import com.arka.carrito_service.domain.exception.*;
import com.arka.carrito_service.infrastructure.adapters.exceptions.CarritoNoEncontradoException;
import com.arka.carrito_service.infrastructure.exceptions.dto.ErrorResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler({
            DetalleCarritoNoEncontradoException.class,
            ProductNotFoundException.class,
            StockInsuficienteException.class,
            UsuarioNoEncontradoException.class,
            CarritoNoEncontradoException.class
    })
    public Mono<ResponseEntity<ErrorResponseDto>> handleNotFound(RuntimeException ex) {
        log.error("NOT FOUND: ", ex.getMessage());
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({
            CarritoActivoExistenteException.class,
            CarritoDiferenteDeAbiertoException.class,
            CarritoExpiradoException.class,
            CarritoVacioException.class,
            IllegalArgumentException.class,
            IllegalStateException.class
    })
    public Mono<ResponseEntity<ErrorResponseDto>> handleBadRequest(RuntimeException ex) {
        log.error("BAD REQUEST: {}", ex.getMessage());
        return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorResponseDto>> handleGenericException(Exception ex) {
        log.error("INTERNAL SERVER ERROR: {}", ex.getMessage(), ex);
        return buildErrorResponse("Internal server error: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private Mono<ResponseEntity<ErrorResponseDto>> buildErrorResponse(String message, HttpStatus status) {
        ErrorResponseDto error = new ErrorResponseDto(
                status.value(),
                message,
                LocalDateTime.now()

        );
        return Mono.just(ResponseEntity.status(status).body(error));
    }
}