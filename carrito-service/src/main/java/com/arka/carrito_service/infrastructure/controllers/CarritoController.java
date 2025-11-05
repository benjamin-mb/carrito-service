package com.arka.carrito_service.infrastructure.controllers;

import com.arka.carrito_service.domain.Dto.DtoCarrito;
import com.arka.carrito_service.domain.model.Carrito;
import com.arka.carrito_service.domain.model.Producto;
import com.arka.carrito_service.domain.useCases.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("api/carrito")
public class CarritoController {

    private final ActualizarCantidadDeDetalleCarritoUseCase actualizarCantidadDeDetalleCarritoUseCase;
    private final AgregarProductoAlCarritoUseCase agregarProductoAlCarritoUseCase;
    private final EliminarDetalleUseCase eliminarDetalleUseCase;
    private final FinalizarCarritoUseCase finalizarCarritoUseCase;
    private final ObtenerCarritoUseCase obtenerCarritoUseCase;

    public CarritoController(ActualizarCantidadDeDetalleCarritoUseCase actualizarCantidadDeDetalleCarritoUseCase, AgregarProductoAlCarritoUseCase agregarProductoAlCarritoUseCase, EliminarDetalleUseCase eliminarDetalleUseCase, FinalizarCarritoUseCase finalizarCarritoUseCase, ObtenerCarritoUseCase obtenerCarritoUseCase) {
        this.actualizarCantidadDeDetalleCarritoUseCase = actualizarCantidadDeDetalleCarritoUseCase;
        this.agregarProductoAlCarritoUseCase = agregarProductoAlCarritoUseCase;
        this.eliminarDetalleUseCase = eliminarDetalleUseCase;
        this.finalizarCarritoUseCase = finalizarCarritoUseCase;
        this.obtenerCarritoUseCase = obtenerCarritoUseCase;
    }

    @GetMapping("/{idUsuario}")
    public Mono<ResponseEntity<DtoCarrito>> obtenerCarrito(@PathVariable Integer idUsuario) {
        return obtenerCarritoUseCase.getCarrito(idUsuario)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping("/agregar")
    public Mono<ResponseEntity<Carrito>> agregarProducto(
            @RequestParam Integer idUsuario,
            @RequestParam Integer cantidad,
            @RequestBody Producto producto) {
        return agregarProductoAlCarritoUseCase.execute(idUsuario, cantidad,producto)
                .map(carrito -> ResponseEntity.status(HttpStatus.CREATED).body(carrito));
    }

    @PutMapping("/detalle/{idDetalle}/cantidad/{cantidad}")
    public Mono<ResponseEntity<Carrito>> actualizarDetalle(
            @PathVariable Integer idDetalle,
            @PathVariable Integer cantidad) {
        return actualizarCantidadDeDetalleCarritoUseCase.execute(idDetalle, cantidad)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/detalle/{idDetalle}")
    public Mono<ResponseEntity<Carrito>> eliminarDetalle(@PathVariable Integer idDetalle) {
        return eliminarDetalleUseCase.eliminarDetalle(idDetalle)
                .map(ResponseEntity::ok);
    }

    @PostMapping("/finalizar/{idUsuario}")
    public Mono<ResponseEntity<?>> finalizarCarrito(@PathVariable Integer idUsuario) {
        return finalizarCarritoUseCase.execute(idUsuario)
                .map(ResponseEntity::ok);
    }
}
