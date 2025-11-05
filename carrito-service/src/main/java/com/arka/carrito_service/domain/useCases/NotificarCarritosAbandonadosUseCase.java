package com.arka.carrito_service.domain.useCases;

import com.arka.carrito_service.domain.model.Estado;
import com.arka.carrito_service.domain.model.gateway.CarritoGateway;
import com.arka.carrito_service.domain.model.gateway.NotificacionGateway;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public class NotificarCarritosAbandonadosUseCase {

    private final CarritoGateway carritoGateway;
    private final NotificacionGateway notificacionGateway;

    public NotificarCarritosAbandonadosUseCase(CarritoGateway carritoGateway, NotificacionGateway notificacionGateway) {
        this.carritoGateway = carritoGateway;
        this.notificacionGateway = notificacionGateway;
    }

    public Mono<Void> notifyUsersAboutAbandonedCar(){
        LocalDateTime date=LocalDateTime.now().minusHours(12);
        return carritoGateway.findCarritosAbandonados(date)
                .flatMap(carrito -> {
                    return notificacionGateway.sendNotiOfCarritoAbandonado(carrito);
                }).then();
    }

    public Mono<Void>changeStateToAbandoned(){
        LocalDateTime date=LocalDateTime.now();
        return carritoGateway.findCarritosAbandonados(date)
                .flatMap(carrito -> {
                    carrito.setEstado(Estado.abandonado);
                    return (carritoGateway.save(carrito));
                }).then();
    }


}
