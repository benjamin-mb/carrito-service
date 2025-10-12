package com.arka.carrito_service.domain.useCases;

import com.arka.carrito_service.domain.exception.CarritoExpiradoException;
import com.arka.carrito_service.domain.exception.CarritoVacioException;
import com.arka.carrito_service.domain.model.Carrito;
import com.arka.carrito_service.domain.model.Estado;
import com.arka.carrito_service.domain.model.gateway.CarritoGateway;
import com.arka.carrito_service.domain.model.gateway.EventPublisherGateway;
import com.arka.carrito_service.infrastructure.adapters.exceptions.CarritoNoEncontradoException;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public class FinalizarCarritoUseCase {

    private final CarritoGateway carritoGateway;
    private final EventPublisherGateway eventPublisherGateway;

    public FinalizarCarritoUseCase(CarritoGateway carritoGateway, EventPublisherGateway eventPublisherGateway) {
        this.carritoGateway = carritoGateway;
        this.eventPublisherGateway = eventPublisherGateway;
    }

    public Mono<?> execute(Integer idUsuario) {

        return carritoGateway.findCarritoActivoByIdUsuario(idUsuario)
                .switchIfEmpty(Mono.error(new CarritoNoEncontradoException("No car active for the user")))
                .flatMap(this::validarYFinalizar);
    }

    private Mono<?> validarYFinalizar(Carrito carrito) {

        if (carrito.getExpirado().isBefore(LocalDateTime.now())) {
            return Mono.error(new CarritoExpiradoException(
                    "Car is expired"
            ));
        }

        if (carrito.getDetalles() == null || carrito.getDetalles().isEmpty()) {
           return Mono.just(new CarritoVacioException("Car is empty"));
        }
        carrito.setEstado(Estado.finalizado);
        return carritoGateway.save(carrito)
                .doOnSuccess(carritoFinalizado->{
                   eventPublisherGateway.publishOrderConfirmed(carritoFinalizado);
                   eventPublisherGateway.publishReduceStock(carritoFinalizado);
                });
    }
}
