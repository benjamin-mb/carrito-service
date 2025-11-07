package com.arka.carrito_service.carrito_service;

import com.arka.carrito_service.domain.exception.CarritoDiferenteDeAbiertoException;
import com.arka.carrito_service.domain.exception.DetalleCarritoNoEncontradoException;
import com.arka.carrito_service.domain.exception.ProductNotFoundException;
import com.arka.carrito_service.domain.exception.StockInsuficienteException;
import com.arka.carrito_service.domain.model.Carrito;
import com.arka.carrito_service.domain.model.DetalleCarrito;
import com.arka.carrito_service.domain.model.Estado;
import com.arka.carrito_service.domain.model.Producto;
import com.arka.carrito_service.domain.model.gateway.CarritoGateway;
import com.arka.carrito_service.domain.model.gateway.DetalleCarritoGateway;
import com.arka.carrito_service.domain.model.gateway.ProductoGateway;
import com.arka.carrito_service.domain.useCases.ActualizarCantidadDeDetalleCarritoUseCase;
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
import java.util.*;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Test reactivos para ActualizarCantidadDetalleCarritoUseCase")
public class ActualizarCantidadDetalleCarritoUseCaseTest {

    @Mock
    private CarritoGateway carritoGateway;

    @Mock
    private DetalleCarritoGateway detalleCarritoGateway;

    @Mock
    private ProductoGateway productoGateway;

    @InjectMocks
    private ActualizarCantidadDeDetalleCarritoUseCase actualizarCantidadDetalle;

    private Producto productoTest;
    private Carrito carritoAbierto;
    private Carrito carritoConDetalles;
    private DetalleCarrito detalleExistente;

    @BeforeEach
    void setUp() {
        // Producto de prueba
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

        // Carrito activo de prueba
        carritoAbierto = new Carrito(
                1,
                1,
                LocalDateTime.now(),
                Estado.abierto,
                LocalDateTime.now().plusHours(24),
                new ArrayList<>()
        );

        // Detalle existente en el carrito
        detalleExistente = new DetalleCarrito(
                1,
                carritoAbierto.getIdCarrito(),
                productoTest.getId(),
                5,
                productoTest.getPrecio(),
                productoTest.getPrecio() * 5
        );

        // Carrito con detalles para retornar después de actualizar
        carritoConDetalles = new Carrito(
                carritoAbierto.getIdCarrito(),
                carritoAbierto.getIdUsuario(),
                carritoAbierto.getCreado(),
                carritoAbierto.getEstado(),
                carritoAbierto.getExpirado(),
                List.of(detalleExistente)
        );
    }

//    ===========================
//        CASOS DE EXITO
//    ===========================


    @Test
    @DisplayName("LNO1 - caso exitoso de actualización de cantidad para detalle Carrito")
    void actualizarCantidadDetalleExitoso(){
        Integer nuevaCantidad = 3;

        when(detalleCarritoGateway.findById(detalleExistente.getIdDdetalleCarrito()))
                .thenReturn(Mono.just(detalleExistente));

        when(carritoGateway.findById(detalleExistente.getIdCarrito()))
                .thenReturn(Mono.just(carritoAbierto));

        when(productoGateway.findById(detalleExistente.getIdProducto()))
                .thenReturn(Optional.of(productoTest));

        DetalleCarrito detalleActualizado = new DetalleCarrito(
                detalleExistente.getIdDdetalleCarrito(),
                detalleExistente.getIdCarrito(),
                detalleExistente.getIdProducto(),
                nuevaCantidad,
                detalleExistente.getPrecioUnitario(),
                detalleExistente.getPrecioUnitario() * nuevaCantidad
        );

        when(detalleCarritoGateway.save(any(DetalleCarrito.class)))
                .thenReturn(Mono.just(detalleActualizado));

        Carrito carritoConDetalleActualizado = new Carrito(
                carritoAbierto.getIdCarrito(),
                carritoAbierto.getIdUsuario(),
                carritoAbierto.getCreado(),
                carritoAbierto.getEstado(),
                carritoAbierto.getExpirado(),
                List.of(detalleActualizado)
        );

        when(carritoGateway.findById(detalleExistente.getIdCarrito()))
                .thenReturn(Mono.just(carritoConDetalleActualizado));

        StepVerifier.create(
                        actualizarCantidadDetalle.execute(
                                detalleExistente.getIdDdetalleCarrito(),
                                nuevaCantidad
                        )
                )
                .expectNextMatches(carrito ->
                        !carrito.getDetalles().isEmpty() &&
                                carrito.getDetalles().get(0).getCantidad().equals(nuevaCantidad) &&
                                carrito.getDetalles().get(0).getSubtotal().equals(nuevaCantidad * productoTest.getPrecio())
                )
                .verifyComplete();

        verify(detalleCarritoGateway, times(1)).findById(anyInt());
        verify(carritoGateway, times(2)).findById(anyInt());
        verify(detalleCarritoGateway, times(1)).save(any(DetalleCarrito.class));
    }

//    ======================
//        CASOS DE ERROR
//    ======================

    @Test
    @DisplayName("LN02 - Caso de error con detalle no encontrado")
    void errorDetalleNoEncontrado(){
        Integer idDetalleInexistente = 2;
        Integer nuevaCantidad = 3;

        when(detalleCarritoGateway.findById(idDetalleInexistente))
                .thenReturn(Mono.empty());

        StepVerifier.create(
                        actualizarCantidadDetalle.execute(idDetalleInexistente, nuevaCantidad)
                )
                .expectErrorMatches(error ->
                        error instanceof DetalleCarritoNoEncontradoException &&
                                error.getMessage().contains("Car detail not found: ")
                )
                .verify();

        verify(detalleCarritoGateway, never()).save(any(DetalleCarrito.class));
    }

    @Test
    @DisplayName("LN03 - Caso de error con carrito no abierto")
    void errorDetalleCarritoNoAbierto(){
        Integer nuevaCantidad = 3;

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
                        actualizarCantidadDetalle.execute(
                                detalleExistente.getIdDdetalleCarrito(),
                                nuevaCantidad
                        )
                )
                .expectErrorMatches(error ->
                        error instanceof CarritoDiferenteDeAbiertoException &&
                                error.getMessage().contains("No se puede modificar un carrito con estado: ")
                )
                .verify();

        verify(detalleCarritoGateway, never()).save(any(DetalleCarrito.class));
    }

    @Test
    @DisplayName("LN04 - Caso de error con cantidad menor o igual a 0")
    void errorNuevaCantidadMenorOIgualA0(){
        Integer nuevaCantidad = 0;

        StepVerifier.create(
                        actualizarCantidadDetalle.execute(
                                detalleExistente.getIdDdetalleCarrito(),
                                nuevaCantidad
                        )
                )
                .expectErrorMatches(error ->
                        error instanceof IllegalArgumentException &&
                                error.getMessage().contains("Quantity must be greater than 0")
                )
                .verify();

        verify(detalleCarritoGateway, never()).save(any(DetalleCarrito.class));
    }

    @Test
    @DisplayName("LN05 - Caso de error con stock insuficiente")
    void errorStockInsuficiente(){
        Integer nuevaCantidad = 21;

        when(detalleCarritoGateway.findById(detalleExistente.getIdDdetalleCarrito()))
                .thenReturn(Mono.just(detalleExistente));

        when(carritoGateway.findById(detalleExistente.getIdCarrito()))
                .thenReturn(Mono.just(carritoAbierto));

        when(productoGateway.findById(detalleExistente.getIdProducto()))
                .thenReturn(Optional.of(productoTest));

        StepVerifier.create(
                        actualizarCantidadDetalle.execute(
                                detalleExistente.getIdDdetalleCarrito(),
                                nuevaCantidad
                        )
                )
                .expectErrorMatches(error ->
                        error instanceof StockInsuficienteException &&
                                error.getMessage().contains("Insufficient stock")
                )
                .verify();

        verify(detalleCarritoGateway, never()).save(any(DetalleCarrito.class));
    }

    @Test
    @DisplayName("LN06 - Caso de error con producto no encontrado")
    void errorProductNotFound() {
        Integer nuevaCantidad = 2;

        when(detalleCarritoGateway.findById(detalleExistente.getIdDdetalleCarrito()))
                .thenReturn(Mono.just(detalleExistente));

        when(carritoGateway.findById(detalleExistente.getIdCarrito()))
                .thenReturn(Mono.just(carritoAbierto));

        when(productoGateway.findById(detalleExistente.getIdProducto()))
                .thenReturn(Optional.empty());

        StepVerifier.create(
                        actualizarCantidadDetalle.execute(
                                detalleExistente.getIdDdetalleCarrito(),
                                nuevaCantidad
                        )
                )
                .expectErrorMatches(error ->
                        error instanceof ProductNotFoundException &&
                                error.getMessage().contains("Product not found")
                )
                .verify();

        verify(detalleCarritoGateway, never()).save(any(DetalleCarrito.class));
    }
}
