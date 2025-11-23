package com.arka.carrito_service.domain.useCases;

import com.arka.carrito_service.domain.Dto.DtoCarrito;
import com.arka.carrito_service.domain.Mapper.Mapper;
import com.arka.carrito_service.domain.model.gateway.CarritoGateway;
import com.arka.carrito_service.infrastructure.adapters.exceptions.CarritoNoEncontradoException;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public class ObtenerCarritoUseCase {

     private final CarritoGateway gateway;
     private final Mapper mapper;

    public ObtenerCarritoUseCase(CarritoGateway gateway, Mapper mapper) {
        this.gateway = gateway;
        this.mapper = mapper;
    }

    public Mono<DtoCarrito> getCarrito(Integer idUsuario){
        return gateway.findCarritoActivoByIdUsuario(idUsuario)
                .switchIfEmpty(Mono.error(new CarritoNoEncontradoException("No active cart found for user: " + idUsuario)))
                .flatMap(carrito -> {
                    if (carrito.getExpirado().isBefore(LocalDateTime.now())){return
                    gateway.deleteById(carrito.getIdCarrito())
                            .then(Mono.error(new CarritoNoEncontradoException("there are no active cars")));
                    }
                    DtoCarrito dtoCarrito=mapper.carritoToDto(carrito);
                    return Mono.just(dtoCarrito);
                });
    }
}
