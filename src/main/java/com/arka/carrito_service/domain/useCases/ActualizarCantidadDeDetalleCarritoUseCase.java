package com.arka.carrito_service.domain.useCases;

import com.arka.carrito_service.domain.exception.CarritoDiferenteDeAbiertoException;
import com.arka.carrito_service.domain.exception.DetalleCarritoNoEncontradoException;
import com.arka.carrito_service.domain.exception.ProductNotFoundException;
import com.arka.carrito_service.domain.exception.StockInsuficienteException;
import com.arka.carrito_service.domain.model.Carrito;
import com.arka.carrito_service.domain.model.DetalleCarrito;
import com.arka.carrito_service.domain.model.Estado;
import com.arka.carrito_service.domain.model.Producto;
import com.arka.carrito_service.domain.model.gateway.CarritoGateway;
import com.arka.carrito_service.domain.model.gateway.DetalleCarritoGateway;
import com.arka.carrito_service.domain.model.gateway.ProductoGateway;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

public class ActualizarCantidadDeDetalleCarritoUseCase {

    private final CarritoGateway carritoGateway;
    private final DetalleCarritoGateway detalleCarritoGateway;
    private final ProductoGateway productoGateway;

    public ActualizarCantidadDeDetalleCarritoUseCase(CarritoGateway carritoGateway, DetalleCarritoGateway detalleCarritoGateway, ProductoGateway productoGateway) {
        this.carritoGateway = carritoGateway;
        this.detalleCarritoGateway = detalleCarritoGateway;
        this.productoGateway = productoGateway;
    }

    public Mono<Carrito> execute(Integer idDetalleCarrito, Integer nuevaCantidad) {
        if (nuevaCantidad <= 0) {
            return Mono.error(new IllegalArgumentException("Quantity must be greater than 0"));
        }

        return detalleCarritoGateway.findById(idDetalleCarrito)
                .switchIfEmpty(Mono.error(new DetalleCarritoNoEncontradoException(
                        "Car detail not found: " + idDetalleCarrito
                )))
                .flatMap(detalle -> carritoGateway.findById(detalle.getIdCarrito())
                        .flatMap(carrito -> {
                            if (carrito.getEstado() != Estado.abierto) {
                                return Mono.error(new CarritoDiferenteDeAbiertoException(
                                        "No se puede modificar un carrito con estado: " + carrito.getEstado()
                                ));
                            }
                            return verificarStockYActualizar(detalle, nuevaCantidad);
                        })
                )
                .flatMap(detalle -> carritoGateway.findById(detalle.getIdCarrito()));
    }

    private Mono<DetalleCarrito> verificarStockYActualizar(DetalleCarrito detalle, Integer nuevaCantidad) {

        return Mono.fromCallable(() ->{
                    Producto producto = productoGateway.findById(detalle.getIdProducto())
                            .orElseThrow(() -> new ProductNotFoundException("Product not found"));
                    return producto.getStock();
                })
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(stock -> {
                    if (stock == null || stock < nuevaCantidad) {
                        return Mono.error(new StockInsuficienteException(
                                String.format("Insufficient stock",
                                        stock, nuevaCantidad)
                        ));
                    }

                    detalle.setCantidad(nuevaCantidad);
                    return detalleCarritoGateway.save(detalle);
                });
    }
}
