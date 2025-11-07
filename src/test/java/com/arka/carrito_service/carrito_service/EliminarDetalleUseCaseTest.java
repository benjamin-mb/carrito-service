package com.arka.carrito_service.carrito_service;


import com.arka.carrito_service.domain.exception.CarritoDiferenteDeAbiertoException;
import com.arka.carrito_service.domain.exception.DetalleCarritoNoEncontradoException;
import com.arka.carrito_service.domain.model.Carrito;
import com.arka.carrito_service.domain.model.DetalleCarrito;
import com.arka.carrito_service.domain.model.Estado;
import com.arka.carrito_service.domain.model.Producto;
import com.arka.carrito_service.domain.model.gateway.CarritoGateway;
import com.arka.carrito_service.domain.model.gateway.DetalleCarritoGateway;
import com.arka.carrito_service.domain.useCases.EliminarDetalleUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Test reactivos para EliminarDetalleUseCase")
public class EliminarDetalleUseCaseTest {

    @Mock
    private DetalleCarritoGateway detalleCarritoGateway;

    @Mock
    private CarritoGateway carritoGateway;

    @InjectMocks
    private EliminarDetalleUseCase eliminarDetalleUseCase;

    private Producto productoTest;
    private Carrito carritoAbierto;
    private DetalleCarrito detalleExistente;

    @BeforeEach
    void setUp() {
        productoTest = new Producto(
                1,
                "Laptop HP",
                1500000,
                20,
                "Core i5, 8GB RAM",
                "HP",
                1,
                1
        );

        carritoAbierto = new Carrito(
                1,
                1,
                LocalDateTime.now(),
                Estado.abierto,
                LocalDateTime.now().plusHours(24),
                new ArrayList<>()
        );

        detalleExistente = new DetalleCarrito(
                1,
                carritoAbierto.getIdCarrito(),
                productoTest.getId(),
                5,
                productoTest.getPrecio(),
                productoTest.getPrecio() * 5
        );
    }

//    ===========================
//        CASOS DE EXITO
//    ===========================

    @Test
    @DisplayName("LN01 - Eliminar detalle correctamente de carrito abierto")
    void eliminarDetalleExitoso() {
        when(detalleCarritoGateway.findById(detalleExistente.getIdDdetalleCarrito()))
                .thenReturn(Mono.just(detalleExistente));

        when(carritoGateway.findById(detalleExistente.getIdCarrito()))
                .thenReturn(Mono.just(carritoAbierto));

        when(detalleCarritoGateway.deleteById(detalleExistente.getIdDdetalleCarrito()))
                .thenReturn(Mono.empty());

        Carrito carritoVacio = new Carrito(
                carritoAbierto.getIdCarrito(),
                carritoAbierto.getIdUsuario(),
                carritoAbierto.getCreado(),
                carritoAbierto.getEstado(),
                carritoAbierto.getExpirado(),
                new ArrayList<>()
        );

        when(carritoGateway.findById(detalleExistente.getIdCarrito()))
                .thenReturn(Mono.just(carritoVacio));

        StepVerifier.create(
                        eliminarDetalleUseCase.eliminarDetalle(detalleExistente.getIdDdetalleCarrito())
                )
                .expectNextMatches(carrito ->
                        carrito.getIdCarrito().equals(carritoAbierto.getIdCarrito()) &&
                                carrito.getDetalles().isEmpty() &&
                                carrito.getEstado() == Estado.abierto
                )
                .verifyComplete();

        verify(detalleCarritoGateway, times(1)).findById(detalleExistente.getIdDdetalleCarrito());
        verify(carritoGateway, times(2)).findById(anyInt());
        verify(detalleCarritoGateway, times(1)).deleteById(detalleExistente.getIdDdetalleCarrito());
    }

    @Test
    @DisplayName("LN02 - Eliminar uno de varios detalles del carrito")
    void eliminarUnDetalleDeVarios() {
        DetalleCarrito detalle2 = new DetalleCarrito(
                2,
                carritoAbierto.getIdCarrito(),
                2,
                3,
                500000,
                1500000
        );

        when(detalleCarritoGateway.findById(detalleExistente.getIdDdetalleCarrito()))
                .thenReturn(Mono.just(detalleExistente));

        when(carritoGateway.findById(detalleExistente.getIdCarrito()))
                .thenReturn(Mono.just(carritoAbierto));

        when(detalleCarritoGateway.deleteById(detalleExistente.getIdDdetalleCarrito()))
                .thenReturn(Mono.empty());

        Carrito carritoConUnDetalle = new Carrito(
                carritoAbierto.getIdCarrito(),
                carritoAbierto.getIdUsuario(),
                carritoAbierto.getCreado(),
                carritoAbierto.getEstado(),
                carritoAbierto.getExpirado(),
                List.of(detalle2)
        );

        when(carritoGateway.findById(detalleExistente.getIdCarrito()))
                .thenReturn(Mono.just(carritoConUnDetalle));

        StepVerifier.create(
                        eliminarDetalleUseCase.eliminarDetalle(detalleExistente.getIdDdetalleCarrito())
                )
                .expectNextMatches(carrito ->
                        carrito.getDetalles().size() == 1 &&
                                carrito.getDetalles().get(0).getIdDdetalleCarrito().equals(2)
                )
                .verifyComplete();

        verify(detalleCarritoGateway, times(1)).deleteById(detalleExistente.getIdDdetalleCarrito());
    }

//    ======================
//        CASOS DE ERROR
//    ======================

    @Test
    @DisplayName("LN03 - Error cuando detalle no existe")
    void errorDetalleNoEncontrado() {
        Integer idDetalleInexistente = 999;

        when(detalleCarritoGateway.findById(idDetalleInexistente))
                .thenReturn(Mono.empty());

        StepVerifier.create(
                        eliminarDetalleUseCase.eliminarDetalle(idDetalleInexistente)
                )
                .expectErrorMatches(error ->
                        error instanceof DetalleCarritoNoEncontradoException &&
                                error.getMessage().contains("Car detail not found: " + idDetalleInexistente)
                )
                .verify();

        verify(detalleCarritoGateway, times(1)).findById(idDetalleInexistente);
        verify(detalleCarritoGateway, never()).deleteById(anyInt());
        verify(carritoGateway, never()).findById(anyInt());
    }

    @Test
    @DisplayName("LN04 - Error cuando carrito no está abierto (finalizado)")
    void errorCarritoFinalizado() {
        Carrito carritoFinalizado = new Carrito(
                carritoAbierto.getIdCarrito(),
                carritoAbierto.getIdUsuario(),
                carritoAbierto.getCreado(),
                Estado.finalizado,
                carritoAbierto.getExpirado(),
                Collections.singletonList(detalleExistente)
        );

        when(detalleCarritoGateway.findById(detalleExistente.getIdDdetalleCarrito()))
                .thenReturn(Mono.just(detalleExistente));

        when(carritoGateway.findById(detalleExistente.getIdCarrito()))
                .thenReturn(Mono.just(carritoFinalizado));

        StepVerifier.create(
                        eliminarDetalleUseCase.eliminarDetalle(detalleExistente.getIdDdetalleCarrito())
                )
                .expectErrorMatches(error ->
                        error instanceof CarritoDiferenteDeAbiertoException &&
                                error.getMessage().contains("No se puede modificar un carrito con estado: ")
                )
                .verify();

        verify(detalleCarritoGateway, never()).deleteById(anyInt());
    }

    @Test
    @DisplayName("LN05 - Error cuando carrito está abandonado")
    void errorCarritoAbandonado() {
        Carrito carritoAbandonado = new Carrito(
                carritoAbierto.getIdCarrito(),
                carritoAbierto.getIdUsuario(),
                carritoAbierto.getCreado(),
                Estado.abandonado,
                carritoAbierto.getExpirado(),
                Collections.singletonList(detalleExistente)
        );

        when(detalleCarritoGateway.findById(detalleExistente.getIdDdetalleCarrito()))
                .thenReturn(Mono.just(detalleExistente));

        when(carritoGateway.findById(detalleExistente.getIdCarrito()))
                .thenReturn(Mono.just(carritoAbandonado));

        StepVerifier.create(
                        eliminarDetalleUseCase.eliminarDetalle(detalleExistente.getIdDdetalleCarrito())
                )
                .expectErrorMatches(error ->
                        error instanceof CarritoDiferenteDeAbiertoException &&
                                error.getMessage().contains("No se puede modificar un carrito con estado: ")
                )
                .verify();

        verify(detalleCarritoGateway, never()).deleteById(anyInt());
    }
}