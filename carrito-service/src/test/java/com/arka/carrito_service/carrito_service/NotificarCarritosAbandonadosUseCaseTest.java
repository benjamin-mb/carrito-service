package com.arka.carrito_service.carrito_service;

import com.arka.carrito_service.domain.model.Carrito;
import com.arka.carrito_service.domain.model.DetalleCarrito;
import com.arka.carrito_service.domain.model.Estado;
import com.arka.carrito_service.domain.model.gateway.CarritoGateway;
import com.arka.carrito_service.domain.model.gateway.NotificacionGateway;
import com.arka.carrito_service.domain.useCases.NotificarCarritosAbandonadosUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tets reactivo para notificar carritos abandonados ")
public class NotificarCarritosAbandonadosUseCaseTest {

    @Mock
    private CarritoGateway carritoGateway;

    @Mock
    private NotificacionGateway notificacionGateway;

    @InjectMocks
    private NotificarCarritosAbandonadosUseCase notificarCarritosUseCase;

    private Carrito carrito1;
    private Carrito carrito2;
    private Carrito carrito3;
    private DetalleCarrito detalle1;

    @BeforeEach
    void setUp() {
        detalle1 = new DetalleCarrito(
                1, 1, 1, 2, 1500000, 3000000
        );

        carrito1 = new Carrito(
                1, 1,
                LocalDateTime.now().minusHours(13),
                Estado.abierto,
                LocalDateTime.now().plusHours(11),
                Arrays.asList(detalle1)
        );

        carrito2 = new Carrito(
                2, 2,
                LocalDateTime.now().minusHours(14),
                Estado.abierto,
                LocalDateTime.now().plusHours(10),
                Arrays.asList(detalle1)
        );

        carrito3 = new Carrito(
                3, 3,
                LocalDateTime.now().minusHours(25),
                Estado.abierto,
                LocalDateTime.now().minusHours(1),
                Arrays.asList(detalle1)
        );
    }

//    =====================================
//       NOTIFICAR CARRITOS ABANDONADOS
//    =====================================

    @Test
    @DisplayName("LNO1 - notificar multiples carritos exito")
    void notificacionDecCarritosExito(){

        when(carritoGateway.findCarritosAbandonados(any(LocalDateTime.class))).thenReturn(Flux.just(carrito1,carrito2,carrito3));
        when(notificacionGateway.sendNotiOfCarritoAbandonado(any(Carrito.class))).thenReturn(Mono.empty());

        StepVerifier.create(
                notificarCarritosUseCase.notifyUsersAboutAbandonedCar()
        ).verifyComplete();

        verify(notificacionGateway,times(3)).sendNotiOfCarritoAbandonado(any(Carrito.class));
    }

    @Test
    @DisplayName("LNO2 - notificar un carritos exito")
    void notificacionDeCarritoExito(){

        when(carritoGateway.findCarritosAbandonados(any(LocalDateTime.class))).thenReturn(Flux.just(carrito1));
        when(notificacionGateway.sendNotiOfCarritoAbandonado(any(Carrito.class))).thenReturn(Mono.empty());

        StepVerifier.create(
                notificarCarritosUseCase.notifyUsersAboutAbandonedCar()
        ).verifyComplete();

        verify(notificacionGateway,times(1)).sendNotiOfCarritoAbandonado(any(Carrito.class));
    }

    @Test
    @DisplayName("LNO3 - hacer el proceso aunque no hallan carritos abandonados")
    void notificacionDeCarritoIntentoCuandoNoHayCarritos(){

        when(carritoGateway.findCarritosAbandonados(any(LocalDateTime.class))).thenReturn(Flux.empty());
        when(notificacionGateway.sendNotiOfCarritoAbandonado(any(Carrito.class))).thenReturn(Mono.empty());

        StepVerifier.create(
                notificarCarritosUseCase.notifyUsersAboutAbandonedCar()
        ).verifyComplete();

        verify(notificacionGateway,never()).sendNotiOfCarritoAbandonado(any(Carrito.class));
    }

//    =========================================
//       CAMBIAR ESTADO CARRITOS ABANDONADOS
//    =========================================

    @Test
    @DisplayName("LN04 - Cambiar estado de múltiples carritos a abandonado")
    void cambiarEstadoMultiplesCarritosExito() {
        when(carritoGateway.findCarritosAbandonados(any(LocalDateTime.class)))
                .thenReturn(Flux.just(carrito1, carrito2, carrito3));

        when(carritoGateway.save(any(Carrito.class)))
                .thenAnswer(invocation -> {
                    Carrito carrito = invocation.getArgument(0);
                    return Mono.just(carrito);
                });

        StepVerifier.create(
                        notificarCarritosUseCase.changeStateToAbandoned()
                )
                .verifyComplete();

        verify(carritoGateway, times(3)).save(any(Carrito.class));

        verify(carritoGateway).save(argThat(carrito ->
                carrito.getEstado() == Estado.abandonado
        ));
    }

    @Test
    @DisplayName("LN05 - Cambiar estado de un solo carrito")
    void cambiarEstadoUnSoloCarrito() {
        when(carritoGateway.findCarritosAbandonados(any(LocalDateTime.class)))
                .thenReturn(Flux.just(carrito1));

        when(carritoGateway.save(any(Carrito.class)))
                .thenAnswer(invocation -> {
                    Carrito carrito = invocation.getArgument(0);
                    return Mono.just(carrito);
                });

        StepVerifier.create(
                        notificarCarritosUseCase.changeStateToAbandoned()
                )
                .verifyComplete();

        verify(carritoGateway, times(1)).save(any(Carrito.class));
    }

    @Test
    @DisplayName("LN06 - No cambiar estado si no hay carritos")
    void noCambiarEstadoCuandoNoHayCarritos() {
        when(carritoGateway.findCarritosAbandonados(any(LocalDateTime.class)))
                .thenReturn(Flux.empty());

        StepVerifier.create(
                        notificarCarritosUseCase.changeStateToAbandoned()
                )
                .verifyComplete();

        verify(carritoGateway, never()).save(any(Carrito.class));
    }

}
