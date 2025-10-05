package com.arka.carrito_service.carrito_service.domain.useCases;

import com.arka.carrito_service.carrito_service.domain.exception.DetalleCarritoNoEncontradoException;
import com.arka.carrito_service.carrito_service.domain.model.Carrito;
import com.arka.carrito_service.carrito_service.domain.model.gateway.CarritoGateway;
import com.arka.carrito_service.carrito_service.domain.model.gateway.DetalleCarritoGateway;
import reactor.core.publisher.Mono;

public class EliminarDetalleUseCase {

    private  final DetalleCarritoGateway detalleCarritoGateway;
    private final CarritoGateway carritoGateway;

    public EliminarDetalleUseCase(DetalleCarritoGateway detalleCarritoGateway, CarritoGateway carritoGateway) {
        this.detalleCarritoGateway = detalleCarritoGateway;
        this.carritoGateway = carritoGateway;
    }

    public Mono<Carrito>deleteDetalle(Integer idDetalleCarrito){
        return detalleCarritoGateway.findById(idDetalleCarrito)
                .switchIfEmpty(Mono.error(new DetalleCarritoNoEncontradoException(
                        "Detalle de carrito no encontrado: " + idDetalleCarrito
                )))
                .flatMap(detalle ->
                        detalleCarritoGateway.deleteById(idDetalleCarrito)
                                .thenReturn(detalle.getIdCarrito()) // Retornar el idCarrito para buscar después
                )
                .flatMap(idCarrito -> carritoGateway.findById(idCarrito));

    }
}
