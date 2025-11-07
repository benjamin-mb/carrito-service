package com.arka.carrito_service.carrito_service;

import com.arka.carrito_service.domain.exception.CarritoExpiradoException;
import com.arka.carrito_service.domain.exception.CarritoVacioException;
import com.arka.carrito_service.domain.model.Carrito;
import com.arka.carrito_service.domain.model.DetalleCarrito;
import com.arka.carrito_service.domain.model.Estado;
import com.arka.carrito_service.domain.model.gateway.CarritoGateway;
import com.arka.carrito_service.domain.model.gateway.EventPublisherGateway;
import com.arka.carrito_service.domain.useCases.FinalizarCarritoUseCase;
import com.arka.carrito_service.infrastructure.adapters.exceptions.CarritoNoEncontradoException;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Test reactivo para FinalizarCarritoUseCase")
public class FinalizarCarritoUseCaseTest {

    @Mock
    private CarritoGateway carritoGateway;

    @Mock
    private EventPublisherGateway eventPublisherGateway;

    @InjectMocks
    private FinalizarCarritoUseCase finalizarCarritoUseCase;

    private Carrito carritoAbierto;
    private DetalleCarrito detalle1;
    private DetalleCarrito detalle2;
    private Integer idUsuario;

    @BeforeEach
    void setUp() {
        idUsuario = 1;

        detalle1 = new DetalleCarrito(
                1,
                1,
                1,
                2,
                1500000,
                3000000
        );

        detalle2 = new DetalleCarrito(
                2,
                1,
                2,
                1,
                500000,
                500000
        );

        carritoAbierto = new Carrito(
                1,
                idUsuario,
                LocalDateTime.now(),
                Estado.abierto,
                LocalDateTime.now().plusHours(24),
                Arrays.asList(detalle1, detalle2)
        );
    }

//    ======================
//        CASOS DE EXITO
//    ======================

    @Test
    @DisplayName("LN01 - caso de exito para finalizar un carrito")
    void casoDeExito(){

        when(carritoGateway.findCarritoActivoByIdUsuario(idUsuario)).thenReturn(Mono.just(carritoAbierto));
        when(carritoGateway.save(any(Carrito.class))).thenAnswer(invocation->{
            Carrito carrito=invocation.getArgument(0);
            return Mono.just(carrito);
        });

        StepVerifier.create(
                finalizarCarritoUseCase.execute(idUsuario)
        ).expectNextMatches(resultado->{
            Carrito carrito=(Carrito) resultado;
            return carrito.getEstado() == Estado.finalizado;
        }).verifyComplete();

        verify(eventPublisherGateway, times(1))
                .publishOrderConfirmed(any(Carrito.class));

        verify(eventPublisherGateway, times(1))
                .publishReduceStock(any(Carrito.class));
    }

//    ======================
//        CASOS DE ERROR
//    ======================

    @Test
    @DisplayName("LN02 - Error al tener un carrito inexistente")
    void errorCarritoInexistente(){

        Integer idUsuario=3;
        when(carritoGateway.findCarritoActivoByIdUsuario(idUsuario)).thenReturn(Mono.empty());
        StepVerifier.create(
                finalizarCarritoUseCase.execute(idUsuario)
        ).expectErrorMatches(
                error-> error instanceof CarritoNoEncontradoException
                && error.getMessage().contains("No car active for the user")
        ).verify();

        verify(eventPublisherGateway,never()).publishReduceStock(any(Carrito.class));
        verify(eventPublisherGateway,never()).publishOrderConfirmed(any(Carrito.class));
    }

    @Test
    @DisplayName("LN03 - Error con carrito expirado")
    void errorCarritoExpirado(){

        LocalDateTime fecha=LocalDateTime.now().minusHours(1);
        carritoAbierto = new Carrito(
                1,
                idUsuario,
                LocalDateTime.now(),
                Estado.abierto,
                fecha,
                Arrays.asList(detalle1, detalle2)
        );

        when(carritoGateway.findCarritoActivoByIdUsuario(carritoAbierto.getIdUsuario())).thenReturn(Mono.just(carritoAbierto));
        StepVerifier.create(
                finalizarCarritoUseCase.execute(carritoAbierto.getIdUsuario())
        ).expectErrorMatches(error-> error instanceof CarritoExpiradoException &&
                error.getMessage().contains( "Car is expired")
        ).verify();

        verify(eventPublisherGateway,never()).publishReduceStock(any(Carrito.class));
        verify(eventPublisherGateway,never()).publishOrderConfirmed(any(Carrito.class));

    }

    @Test
    @DisplayName("LN04 - Error con carrito vacio")
    void errorCarritoVacio(){

        carritoAbierto = new Carrito(
                1,
                idUsuario,
                LocalDateTime.now(),
                Estado.abierto,
                LocalDateTime.now().plusHours(24),
                Collections.emptyList()
        );

        when(carritoGateway.findCarritoActivoByIdUsuario(carritoAbierto.getIdUsuario())).thenReturn(Mono.just(carritoAbierto));
        StepVerifier.create(finalizarCarritoUseCase.execute(carritoAbierto.getIdUsuario())).expectErrorMatches(
                error->error instanceof CarritoVacioException &&
                        error.getMessage().contains("Car is empty")
        ).verify();

        verify(eventPublisherGateway,never()).publishReduceStock(any(Carrito.class));
        verify(eventPublisherGateway,never()).publishOrderConfirmed(any(Carrito.class));
    }
}
