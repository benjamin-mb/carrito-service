package com.arka.carrito_service.infrastructure.adapters.repository;

import com.arka.carrito_service.domain.model.Carrito;
import com.arka.carrito_service.domain.model.DetalleCarrito;
import com.arka.carrito_service.domain.model.gateway.EventPublisherGateway;
import com.arka.carrito_service.infrastructure.Dto.CrearOrdenEventDto;
import com.arka.carrito_service.infrastructure.Dto.DetalleOrdenDto;
import com.arka.carrito_service.infrastructure.Dto.DetalleReduceStockDto;
import com.arka.carrito_service.infrastructure.Dto.DetallesDto;
import com.arka.carrito_service.infrastructure.messages.OrdenPublisher;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class EventPublisherAdapters implements EventPublisherGateway {

    private  final OrdenPublisher ordenPublisher;

    public EventPublisherAdapters(OrdenPublisher ordenPublisher) {
        this.ordenPublisher = ordenPublisher;
    }

    @Override
    public void publishOrderConfirmed(Carrito carrito) {

        List<DetalleOrdenDto>detalles=carrito.getDetalles()
                .stream().map(detalleCarrito -> new DetalleOrdenDto(
                            detalleCarrito.getIdProducto(),
                            detalleCarrito.getCantidad(),
                            detalleCarrito.getPrecioUnitario(),
                            detalleCarrito.getSubtotal()
                    )).collect(Collectors.toList());
        Integer montoTotal=carrito.getDetalles().stream()
                .mapToInt(DetalleCarrito::getSubtotal)
                .sum();

        CrearOrdenEventDto event=new CrearOrdenEventDto(
                carrito.getIdUsuario(),
                montoTotal,
                LocalDateTime.now(),
               carrito.getIdCarrito(),
                detalles
        );

        ordenPublisher.publishOrderConfirmed(event);
    }

    @Override
    public void publishReduceStock(Carrito carrito) {

        List <DetalleReduceStockDto> items= carrito.getDetalles()
                .stream().map(detail->new DetalleReduceStockDto(
                        detail.getIdProducto(),
                        detail.getCantidad()
                )).collect(Collectors.toList());

        DetallesDto detallesDto= new DetallesDto(items);

        ordenPublisher.publishReduceStock(detallesDto);

    }
}
