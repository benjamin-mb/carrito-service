package com.arka.carrito_service.carrito_service;

import com.arka.carrito_service.domain.Dto.DtoCarrito;
import com.arka.carrito_service.domain.Mapper.Mapper;
import com.arka.carrito_service.domain.model.Carrito;
import com.arka.carrito_service.domain.model.DetalleCarrito;
import com.arka.carrito_service.domain.model.Estado;
import com.arka.carrito_service.domain.model.gateway.CarritoGateway;
import com.arka.carrito_service.domain.useCases.ObtenerCarritoUseCase;
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
import java.util.ArrayList;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Test reactivos para ObtenerCarritoUseCase")
public class ObtenerCarritoUseCaseTest {

    @Mock
    private CarritoGateway carritoGateway;

    @Mock
    private Mapper mapper;

    @InjectMocks
    private ObtenerCarritoUseCase obtenerCarritoUseCase;

    private Carrito carritoValido;
    private DetalleCarrito detalle1;
    private DetalleCarrito detalle2;
    private DtoCarrito dtoCarrito;
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

        carritoValido = new Carrito(
                1,
                idUsuario,
                LocalDateTime.now(),
                Estado.abierto,
                LocalDateTime.now().plusHours(24),
                Arrays.asList(detalle1, detalle2)
        );

        dtoCarrito = new DtoCarrito(
                carritoValido.getCreado(),
                carritoValido.getEstado(),
                carritoValido.getDetalles(),
                3500000
        );
    }

//    ======================
//        CASOS DE EXITO
//    ======================

    @Test
    @DisplayName("LN01 - Obtener carrito válido correctamente")
    void obtenerCarritoValidoExitoso() {
        when(carritoGateway.findCarritoActivoByIdUsuario(idUsuario))
                .thenReturn(Mono.just(carritoValido));

        when(mapper.carritoToDto(any(Carrito.class)))
                .thenReturn(dtoCarrito);

        StepVerifier.create(
                        obtenerCarritoUseCase.getCarrito(idUsuario)
                )
                .expectNextMatches(dto ->
                        dto.getEstado() == Estado.abierto &&
                                dto.getDetalles().size() == 2 &&
                                dto.getMontoTotal().equals(3500000)
                )
                .verifyComplete();

        verify(carritoGateway, times(1)).findCarritoActivoByIdUsuario(idUsuario);
        verify(mapper, times(1)).carritoToDto(any(Carrito.class));
        verify(carritoGateway, never()).deleteById(anyInt());
    }

    @Test
    @DisplayName("LN02 - Verificar que mapper recibe el carrito correcto")
    void verificarMapperRecibeCarritoCorrecto() {
        when(carritoGateway.findCarritoActivoByIdUsuario(idUsuario))
                .thenReturn(Mono.just(carritoValido));

        when(mapper.carritoToDto(any(Carrito.class)))
                .thenReturn(dtoCarrito);

        StepVerifier.create(
                        obtenerCarritoUseCase.getCarrito(idUsuario)
                )
                .expectNextCount(1)
                .verifyComplete();

        verify(mapper).carritoToDto(argThat(carrito ->
                carrito.getIdCarrito().equals(carritoValido.getIdCarrito()) &&
                        carrito.getIdUsuario().equals(idUsuario) &&
                        carrito.getEstado() == Estado.abierto
        ));
    }

    @Test
    @DisplayName("LN03 - Flujo completo: buscar, mapear y retornar DTO")
    void flujoCompletoExitoso() {
        when(carritoGateway.findCarritoActivoByIdUsuario(idUsuario))
                .thenReturn(Mono.just(carritoValido));

        when(mapper.carritoToDto(carritoValido))
                .thenReturn(dtoCarrito);

        StepVerifier.create(
                        obtenerCarritoUseCase.getCarrito(idUsuario)
                )
                .expectNext(dtoCarrito)
                .verifyComplete();

        verify(carritoGateway, times(1)).findCarritoActivoByIdUsuario(idUsuario);
        verify(mapper, times(1)).carritoToDto(carritoValido);
        verify(carritoGateway, never()).deleteById(anyInt());
    }

    @Test
    @DisplayName("LN04 - Verificar que el DTO contiene toda la información del carrito")
    void verificarDtoContieneTodaLaInformacion() {
        when(carritoGateway.findCarritoActivoByIdUsuario(idUsuario))
                .thenReturn(Mono.just(carritoValido));

        when(mapper.carritoToDto(any(Carrito.class)))
                .thenReturn(dtoCarrito);

        StepVerifier.create(
                        obtenerCarritoUseCase.getCarrito(idUsuario)
                )
                .expectNextMatches(dto ->
                        dto.getCreado() != null &&
                                dto.getEstado() != null &&
                                dto.getDetalles() != null &&
                                dto.getMontoTotal() != null &&
                                dto.getDetalles().size() == 2
                )
                .verifyComplete();
    }

//    ======================
//        CASOS DE ERROR
//    ======================

    @Test
    @DisplayName("LN05 - Eliminar y lanzar error cuando carrito está expirado")
    void eliminarCarritoExpirado() {
        Carrito carritoExpirado = new Carrito(
                1,
                idUsuario,
                LocalDateTime.now().minusHours(25),
                Estado.abierto,
                LocalDateTime.now().minusHours(1),
                Arrays.asList(detalle1, detalle2)
        );

        when(carritoGateway.findCarritoActivoByIdUsuario(idUsuario))
                .thenReturn(Mono.just(carritoExpirado));

        when(carritoGateway.deleteById(carritoExpirado.getIdCarrito()))
                .thenReturn(Mono.empty());

        StepVerifier.create(
                        obtenerCarritoUseCase.getCarrito(idUsuario)
                )
                .expectErrorMatches(error ->
                        error instanceof CarritoNoEncontradoException &&
                                error.getMessage().contains("there are no active cars")
                )
                .verify();

        verify(carritoGateway, times(1)).findCarritoActivoByIdUsuario(idUsuario);
        verify(carritoGateway, times(1)).deleteById(carritoExpirado.getIdCarrito());
        verify(mapper, never()).carritoToDto(any(Carrito.class));
    }

}
