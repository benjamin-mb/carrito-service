package com.arka.carrito_service.carrito_service.infraestructure.adapters.mapper;

import com.arka.carrito_service.carrito_service.domain.model.Carrito;
import com.arka.carrito_service.carrito_service.domain.model.Estado;
import com.arka.carrito_service.carrito_service.infraestructure.adapters.entity.CarritoEntity;
import com.arka.carrito_service.carrito_service.infraestructure.adapters.entity.EstadoEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Component
public class CarritoMapper {

    private final DetalleMapper detalleMapper;

    public CarritoMapper(DetalleMapper detalleMapper) {
        this.detalleMapper = detalleMapper;
    }

    public Carrito toDomain(CarritoEntity entity) {
        if (entity == null) return null;

        return new Carrito(
                entity.getIdCarrito(),
                entity.getIdUsuario(),
                entity.getCreado(),
                mapEstadoToDomain(entity.getEstado()),
                entity.getExpirado(),
                entity.getDetalles() != null
                        ? entity.getDetalles().stream()
                        .map(detalleMapper::toDomain)
                        .collect(Collectors.toList())
                        : new ArrayList<>()
        );
    }

    public CarritoEntity toEntity(Carrito carrito) {
        if (carrito == null) return null;

        CarritoEntity entity = new CarritoEntity(
                carrito.getIdUsuario(),
                carrito.getCreado(),
                mapEstadoToEntity(carrito.getEstado()),
                carrito.getExpirado()
        );
        entity.setIdCarrito(carrito.getIdCarrito());

        return entity;
    }

    private Estado mapEstadoToDomain(EstadoEntity estadoEntity) {
        return switch (estadoEntity) {
            case abierto -> Estado.abierto;
            case finalizado -> Estado.finalizado;
            case abandonado -> Estado.finalizado;
        };
    }

    private EstadoEntity mapEstadoToEntity(Estado estado) {
        return switch (estado) {
            case abierto -> EstadoEntity.abierto;
            case finalizado -> EstadoEntity.finalizado;
            case abandonado -> EstadoEntity.abandonado;
        };
    }
}
