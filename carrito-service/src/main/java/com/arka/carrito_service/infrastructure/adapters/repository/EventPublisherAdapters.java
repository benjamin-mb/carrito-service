package com.arka.carrito_service.infrastructure.adapters.repository;

import com.arka.carrito_service.domain.model.Carrito;
import com.arka.carrito_service.domain.model.DetalleCarrito;
import com.arka.carrito_service.domain.model.gateway.EventPublisherGateway;
import com.arka.carrito_service.infrastructure.messages.Dto.CrearOrdenEventDto;
import com.arka.carrito_service.infrastructure.messages.Dto.DetalleOrdenDto;
import com.arka.carrito_service.infrastructure.messages.Dto.DetalleReduceStockDto;
import com.arka.carrito_service.infrastructure.messages.Dto.DetallesDto;
import com.arka.carrito_service.infrastructure.messages.OrderPublisher;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class EventPublisherAdapters implements EventPublisherGateway {

    private  final OrderPublisher orderPublisher;

    public EventPublisherAdapters(OrderPublisher orderPublisher) {
        this.orderPublisher = orderPublisher;
    }

    @Override
    public void publishOrderConfirmed(Carrito carrito) {
        List<DetalleOrdenDto>details=carrito.getDetalles().stream()
                .map(detail->new DetalleOrdenDto(
                        detail.getIdProducto(),
                        detail.getCantidad(),
                        detail.getPrecioUnitario(),
                        detail.getSubtotal()
                )).collect(Collectors.toList());

        Integer montoTotal=carrito.getDetalles().stream()
                .mapToInt(DetalleCarrito::getSubtotal)
                .sum();

        CrearOrdenEventDto event=new CrearOrdenEventDto(
                carrito.getIdUsuario(),
                montoTotal,
                LocalDateTime.now(),
                details
        );

        orderPublisher.publishOrderConfirmed(event);
    }

    @Override
    public void publishReduceStock(Carrito carrito) {

        List <DetalleReduceStockDto> items= carrito.getDetalles()
                .stream().map(detail->new DetalleReduceStockDto(
                        detail.getIdProducto(),
                        detail.getCantidad()
                )).collect(Collectors.toList());

        DetallesDto detallesDto= new DetallesDto(items);

        orderPublisher.publishReduceStock(detallesDto);

    }
}
