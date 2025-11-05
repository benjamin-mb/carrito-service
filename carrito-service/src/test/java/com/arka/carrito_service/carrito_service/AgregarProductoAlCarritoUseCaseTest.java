package com.arka.carrito_service.carrito_service;

import com.arka.carrito_service.domain.exception.ProductNotFoundException;
import com.arka.carrito_service.domain.exception.StockInsuficienteException;
import com.arka.carrito_service.domain.exception.UsuarioNoEncontradoException;
import com.arka.carrito_service.domain.model.Carrito;
import com.arka.carrito_service.domain.model.DetalleCarrito;
import com.arka.carrito_service.domain.model.Estado;
import com.arka.carrito_service.domain.model.Producto;
import com.arka.carrito_service.domain.model.gateway.CarritoGateway;
import com.arka.carrito_service.domain.model.gateway.DetalleCarritoGateway;
import com.arka.carrito_service.domain.model.gateway.ProductoGateway;
import com.arka.carrito_service.domain.model.gateway.UsuarioGateway;
import com.arka.carrito_service.domain.useCases.AgregarProductoAlCarritoUseCase;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests reactivos para AgregarProductoAlCarritoUseCase")
public class AgregarProductoAlCarritoUseCaseTest {

    @Mock
    private UsuarioGateway usuarioGateway;

    @Mock
    private ProductoGateway productoGateway;

    @Mock
    private CarritoGateway carritoGateway;

    @Mock
    private DetalleCarritoGateway detalleCarritoGateway;

    @InjectMocks
    private AgregarProductoAlCarritoUseCase agregarProductoUseCase;

    private Producto productoTest;
    private Carrito carritoActivo;
    private DetalleCarrito detalleTest;
    private Integer idUsuario;

    @BeforeEach
    void setUp() {
        idUsuario = 1;

        productoTest = new Producto(
                1,
                "Laptop HP",
                1500000,
                10,
                "Core i5, 8GB RAM",
                "HP",
                1,
                1
        );

        carritoActivo = new Carrito(
                1,
                idUsuario,
                LocalDateTime.now(),
                Estado.abierto,
                LocalDateTime.now().plusHours(24),
                new ArrayList<>()
        );

        detalleTest = new DetalleCarrito(
                1,
                carritoActivo.getIdCarrito(),
                productoTest.getId(),
                2,
                productoTest.getPrecio(),
                productoTest.getPrecio() * 2
        );
    }
//    ======================
//        CASOS DE EXITO
//    ======================

    @Test
    @DisplayName("LN01 - Debe crear carrito nuevo y agregar producto cuando no existe carrito activo")
    void createCarritoExitoS() {

        Integer cantidad = 2;

        when(carritoGateway.findCarritoActivoByIdUsuario(idUsuario))
                .thenReturn(Mono.empty());

        when(usuarioGateway.existsById(idUsuario))
                .thenReturn(true);

        when(carritoGateway.save(any(Carrito.class)))
                .thenReturn(Mono.just(carritoActivo));

        when(productoGateway.existsById(productoTest.getId()))
                .thenReturn(true);

        when(detalleCarritoGateway.findByIdCarritoAndIdProducto(
                anyInt(), anyInt()))
                .thenReturn(Mono.empty());

        when(detalleCarritoGateway.save(any(DetalleCarrito.class)))
                .thenReturn(Mono.just(detalleTest));

        Carrito carritoConDetalles = new Carrito(
                carritoActivo.getIdCarrito(),
                carritoActivo.getIdUsuario(),
                carritoActivo.getCreado(),
                carritoActivo.getEstado(),
                carritoActivo.getExpirado(),
                Collections.singletonList(detalleTest)
        );

        when(carritoGateway.findById(carritoActivo.getIdCarrito()))
                .thenReturn(Mono.just(carritoConDetalles));


        StepVerifier.create(
                        agregarProductoUseCase.execute(idUsuario, cantidad, productoTest)
                )
                .expectNextMatches(carrito ->
                        carrito.getIdUsuario().equals(idUsuario) &&
                                carrito.getEstado() == Estado.abierto &&
                                !carrito.getDetalles().isEmpty()
                )
                .verifyComplete();

        verify(carritoGateway, times(1)).findCarritoActivoByIdUsuario(idUsuario);
        verify(usuarioGateway, times(1)).existsById(idUsuario);
        verify(carritoGateway, times(1)).save(any(Carrito.class));
        verify(detalleCarritoGateway, times(1)).save(any(DetalleCarrito.class));
    }

    @Test
    @DisplayName("LN02 - Debe agregar producto nuevo al carrito existente")
    void addProdductExitosoCarritoYaCreado() {

        Integer cantidad = 3;

        when(carritoGateway.findCarritoActivoByIdUsuario(idUsuario))
                .thenReturn(Mono.just(carritoActivo));

        when(productoGateway.existsById(productoTest.getId()))
                .thenReturn(true);

        when(detalleCarritoGateway.findByIdCarritoAndIdProducto(
                carritoActivo.getIdCarrito(),
                productoTest.getId()))
                .thenReturn(Mono.empty());

        when(detalleCarritoGateway.save(any(DetalleCarrito.class)))
                .thenReturn(Mono.just(detalleTest));

        Carrito carritoConDetalles = new Carrito(
                carritoActivo.getIdCarrito(),
                carritoActivo.getIdUsuario(),
                carritoActivo.getCreado(),
                carritoActivo.getEstado(),
                carritoActivo.getExpirado(),
                Collections.singletonList(detalleTest)
        );

        when(carritoGateway.findById(carritoActivo.getIdCarrito()))
                .thenReturn(Mono.just(carritoConDetalles));

        StepVerifier.create(
                        agregarProductoUseCase.execute(idUsuario, cantidad, productoTest)
                )
                .expectNextMatches(carrito ->
                        carrito.getDetalles().size() > 0 &&
                                carrito.getEstado() == Estado.abierto
                )
                .verifyComplete();

        verify(detalleCarritoGateway, times(1)).save(any(DetalleCarrito.class));
        verify(carritoGateway, never()).save(argThat(c -> c.getIdCarrito() == null
        ));
    }

    @Test
    @DisplayName("LN03 - Debe actualizar cantidad cuando producto ya existe en carrito")
    void updateCantidadCuandoProductoYaEnCarrito() {

        Integer cantidadAdicional = 2;
        Integer cantidadOriginal = 3;

        DetalleCarrito detalleExistente = new DetalleCarrito(
                1,
                carritoActivo.getIdCarrito(),
                productoTest.getId(),
                cantidadOriginal,
                productoTest.getPrecio(),
                productoTest.getPrecio() * cantidadOriginal
        );

        when(carritoGateway.findCarritoActivoByIdUsuario(idUsuario))
                .thenReturn(Mono.just(carritoActivo));

        when(productoGateway.existsById(productoTest.getId()))
                .thenReturn(true);

        when(detalleCarritoGateway.findByIdCarritoAndIdProducto(
                carritoActivo.getIdCarrito(),
                productoTest.getId()))
                .thenReturn(Mono.just(detalleExistente));

        DetalleCarrito detalleActualizado = new DetalleCarrito(
                detalleExistente.getIdDdetalleCarrito(),
                detalleExistente.getIdCarrito(),
                detalleExistente.getIdProducto(),
                cantidadOriginal + cantidadAdicional,
                detalleExistente.getPrecioUnitario(),
                detalleExistente.getPrecioUnitario() * (cantidadOriginal + cantidadAdicional)
        );

        when(detalleCarritoGateway.save(any(DetalleCarrito.class)))
                .thenReturn(Mono.just(detalleActualizado));

        when(carritoGateway.findById(carritoActivo.getIdCarrito()))
                .thenReturn(Mono.just(carritoActivo));

        StepVerifier.create(
                        agregarProductoUseCase.execute(idUsuario, cantidadAdicional, productoTest)
                )
                .expectNextMatches(carrito ->
                        carrito.getIdCarrito().equals(carritoActivo.getIdCarrito())
                )
                .verifyComplete();

        verify(detalleCarritoGateway, times(1)).save(argThat(detalle ->
                detalle.getCantidad().equals(cantidadOriginal + cantidadAdicional)
        ));
    }

//    ======================
//        CASOS DE ERROR
//    ======================

    @Test
    @DisplayName("LN04 - Debe lanzar UsuarioNoEncontradoException cuando usuario no existe")
    void errorCuandoUsuarioNoEncontrado() {

        when(carritoGateway.findCarritoActivoByIdUsuario(idUsuario))
                .thenReturn(Mono.empty());

        when(usuarioGateway.existsById(idUsuario))
                .thenReturn(false);

        StepVerifier.create(
                        agregarProductoUseCase.execute(idUsuario, 2, productoTest)
                )
                .expectErrorMatches(error ->
                        error instanceof UsuarioNoEncontradoException &&
                                error.getMessage().contains("User does not exist")
                )
                .verify();

        verify(detalleCarritoGateway, never()).save(any());
        verify(carritoGateway, never()).save(argThat(c -> c.getIdCarrito() != null));
    }

    @Test
    @DisplayName("LN05 - Debe lanzar ProductNotFoundException cuando producto no existe")
    void errorCuandoProductoNoEncontrado() {

        when(carritoGateway.findCarritoActivoByIdUsuario(idUsuario))
                .thenReturn(Mono.just(carritoActivo));

        when(productoGateway.existsById(productoTest.getId()))
                .thenReturn(false);

        StepVerifier.create(
                        agregarProductoUseCase.execute(idUsuario, 2, productoTest)
                )
                .expectErrorMatches(error ->
                        error instanceof ProductNotFoundException &&
                                error.getMessage().contains("product not found")
                )
                .verify();

        verify(detalleCarritoGateway, never()).save(any());
    }

    @Test
    @DisplayName("LN06 - Debe lanzar StockInsuficienteException cuando no hay stock")
    void errorCuandoStockInsuficiente() {

        Producto productoConPocoStock = new Producto(
                1, "Laptop", 1000000, 5, "Test", "HP", 1, 1
        );
        Integer cantidadSolicitada = 10;

        when(carritoGateway.findCarritoActivoByIdUsuario(idUsuario))
                .thenReturn(Mono.just(carritoActivo));

        when(productoGateway.existsById(productoConPocoStock.getId()))
                .thenReturn(true);

        StepVerifier.create(
                        agregarProductoUseCase.execute(idUsuario, cantidadSolicitada, productoConPocoStock)
                )
                .expectErrorMatches(error ->
                        error instanceof StockInsuficienteException &&
                                error.getMessage().contains("Insufficient Stock")
                )
                .verify();

        verify(detalleCarritoGateway, never()).save(any());
    }

}
