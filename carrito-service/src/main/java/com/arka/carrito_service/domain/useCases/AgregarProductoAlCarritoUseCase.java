package com.arka.carrito_service.domain.useCases;

import com.arka.carrito_service.domain.exception.ProductNotFoundException;
import com.arka.carrito_service.domain.exception.StockInsuficienteException;
import com.arka.carrito_service.domain.exception.UsuarioNoEncontradoException;
import com.arka.carrito_service.domain.model.Carrito;
import com.arka.carrito_service.domain.model.DetalleCarrito;
import com.arka.carrito_service.domain.model.Estado;
import com.arka.carrito_service.domain.model.Producto;
import com.arka.carrito_service.domain.model.gateway.CarritoGateway;
import com.arka.carrito_service.domain.model.gateway.DetalleCarritoGateway;
import com.arka.carrito_service.domain.model.gateway.ProductoGateway;
import com.arka.carrito_service.domain.model.gateway.UsuarioGateway;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class AgregarProductoAlCarritoUseCase {

    private final UsuarioGateway usuarioGateway;
    private final ProductoGateway productoGateway;
    private final CarritoGateway carritoGateway;
    private final DetalleCarritoGateway detalleCarritoGateway;

    public AgregarProductoAlCarritoUseCase(UsuarioGateway usuarioGateway, ProductoGateway productoGateway, CarritoGateway carritoGateway, DetalleCarritoGateway detalleCarritoGateway) {
        this.usuarioGateway = usuarioGateway;
        this.productoGateway = productoGateway;
        this.carritoGateway = carritoGateway;
        this.detalleCarritoGateway = detalleCarritoGateway;
    }

    public Mono<Carrito> execute(Integer idUsuario, Producto producto, Integer cantidad) {

        return obtenerOCrearCarritoValido(idUsuario)
                .flatMap(carrito -> agregarOActualizarProducto(carrito, producto, cantidad));
    }

    private Mono<Carrito> obtenerOCrearCarritoValido(Integer idUsuario) {
        return carritoGateway.findCarritoActivoByIdUsuario(idUsuario)
                .flatMap(carrito -> {
                    if (carrito.getExpirado().isBefore(LocalDateTime.now())) {
                        return carritoGateway.deleteById(carrito.getIdCarrito())
                                .then(crearCarrito(idUsuario));
                    }
                    return Mono.just(carrito);
                })
                .switchIfEmpty(crearCarrito(idUsuario));
    }
    private Mono<Carrito>crearCarrito(Integer id_usuario){
        return Mono.fromCallable(() -> usuarioGateway.existsById(id_usuario))
                .flatMap(existe -> {
                    if (Boolean.FALSE.equals(existe)) {
                        return Mono.error(new UsuarioNoEncontradoException(
                                "User does not exist: " + id_usuario
                        ));
                    }

                    Carrito nuevoCarrito = new Carrito(
                            id_usuario,
                            LocalDateTime.now(),
                            Estado.abierto,
                            LocalDateTime.now().plusHours(24),
                            new ArrayList<>()
                    );

                    return carritoGateway.save(nuevoCarrito);
                });
    }

    private Mono<Carrito> agregarOActualizarProducto(Carrito carrito, Producto producto, Integer cantidad) {

        if (carrito.getEstado().equals(Estado.finalizado)){
            return Mono.error(new IllegalStateException("a car that was already confirmed can´t be changed"));
        }
        return Mono.fromCallable(() -> {
                    Producto productoAObtener=producto;
                    if (!productoGateway.existsById(productoAObtener.getId())){
                        throw new ProductNotFoundException("product not found");
                    }
                    Integer stock=productoAObtener.getStock();
                    if (stock < cantidad) {
                        throw new StockInsuficienteException(
                                String.format("Insufficient Stock",
                                        productoAObtener.getId(), stock, cantidad)
                        );
                    }

                    return producto.getPrecio();
                })
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(precio -> {

                    return detalleCarritoGateway.findByIdCarritoAndIdProducto(
                                    carrito.getIdCarrito(),
                                    producto.getId()
                            )
                            .flatMap(detalleExistente -> {
                                detalleExistente.setCantidad(detalleExistente.getCantidad() + cantidad);
                                return detalleCarritoGateway.save(detalleExistente);
                            })
                            .switchIfEmpty(
                                    Mono.defer(() -> {
                                        DetalleCarrito nuevoDetalle = new DetalleCarrito(
                                                carrito.getIdCarrito(),
                                                producto.getId(),
                                                cantidad,
                                                precio,
                                                cantidad*precio
                                        );
                                        return detalleCarritoGateway.save(nuevoDetalle);
                                    })
                            );
                })
                .then(carritoGateway.findById(carrito.getIdCarrito()));
    }

}
