package com.arka.carrito_service.carrito_service.domain.useCases;

import com.arka.carrito_service.carrito_service.domain.exception.CarritoActivoExistenteException;
import com.arka.carrito_service.carrito_service.domain.exception.UsuarioNoEncontradoException;
import com.arka.carrito_service.carrito_service.domain.model.Carrito;
import com.arka.carrito_service.carrito_service.domain.model.Estado;
import com.arka.carrito_service.carrito_service.domain.model.gateway.CarritoGateway;
import com.arka.carrito_service.carrito_service.domain.model.gateway.ProductoGateway;
import com.arka.carrito_service.carrito_service.domain.model.gateway.UsuarioGateway;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class CrearCarritoUseCase {

    private final CarritoGateway gateway;
    private final UsuarioGateway usuarioGateway;

    public CrearCarritoUseCase(CarritoGateway gateway, UsuarioGateway usuarioGateway) {
        this.gateway = gateway;
        this.usuarioGateway = usuarioGateway;
    }

    public Mono<Carrito> execute (Integer id_usuario){
        return gateway.findCarritoActivo(id_usuario)
                .flatMap(existe->{
                    if (existe){
                        return Mono.error(new CarritoActivoExistenteException("ya hay un carrito existente para este usuario"));
                    }
                    return createCarrito(id_usuario);
                });

    }

    private Mono<Carrito> createCarrito(Integer idUsuario) {
        return Mono.fromCallable(() -> usuarioGateway.existsById(idUsuario))
                .flatMap(existe -> {
                    if (Boolean.FALSE.equals(existe)) {
                        return Mono.error(new UsuarioNoEncontradoException(
                                "El usuario no existe: " + idUsuario
                        ));
                    }

                    Carrito nuevoCarrito = new Carrito(
                            idUsuario,
                            LocalDateTime.now(),
                            Estado.abierto,
                            LocalDateTime.now().plusHours(24),
                            new ArrayList<>()
                    );

                    return gateway.save(nuevoCarrito); // Reactivo
                });
    }
}
