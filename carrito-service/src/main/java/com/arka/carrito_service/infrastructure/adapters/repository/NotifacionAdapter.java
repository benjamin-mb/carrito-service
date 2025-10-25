package com.arka.carrito_service.infrastructure.adapters.repository;

import com.arka.carrito_service.domain.model.Carrito;
import com.arka.carrito_service.domain.model.DetalleCarrito;
import com.arka.carrito_service.domain.model.gateway.NotificacionGateway;
import com.arka.carrito_service.domain.model.gateway.ProductoGateway;
import com.arka.carrito_service.domain.model.gateway.UsuarioGateway;
import com.arka.carrito_service.infrastructure.Dto.CarritoAbandonadoDto;
import com.arka.carrito_service.infrastructure.Dto.DetalleWeebhookDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public class NotifacionAdapter implements NotificacionGateway {

    private static final Logger log = LoggerFactory.getLogger(NotifacionAdapter.class);
    private final WebClient webClient;
    private final  String webhookUrl;
    private final ProductoGateway productoGateway;
    private final UsuarioGateway usuarioGateway;


    public NotifacionAdapter(WebClient.Builder webClient, @Value("${notificacion.webhook.url}")String webhookUrl, ProductoGateway productoGateway, UsuarioGateway usuarioGateway) {
        this.webClient = webClient.build();
        this.webhookUrl=webhookUrl;
        this.productoGateway = productoGateway;
        this.usuarioGateway = usuarioGateway;
    }

    @Override
    public Mono<Void> sendNotiOfCarritoAbandonado(Carrito carrito) {

        return Mono.fromCallable(() -> usuarioGateway.findById(carrito.getIdUsuario()).orElseThrow(() -> new RuntimeException("Usuario no encontrado")).getEmail())
                .flatMap(email -> Flux.fromIterable(carrito.getDetalles())
                        .flatMap(detalle -> Mono.fromCallable(() -> {
                            String nombreProducto = productoGateway.findById(detalle.getIdProducto()).map(p -> p.getNombre()).orElse("Producto desconocido");
                            DetalleWeebhookDto dto = new DetalleWeebhookDto();
                            dto.setNombreProducto(nombreProducto);
                            dto.setCantidad(detalle.getCantidad());
                            dto.setSubtotal(detalle.getSubtotal());
                            return dto;
                        }))
                        .collectList()
                        .map(detalles -> {
                            Integer total = carrito.getDetalles().stream().mapToInt(DetalleCarrito::getSubtotal).sum();
                            CarritoAbandonadoDto dto = new CarritoAbandonadoDto();
                            dto.setUsuarioEmail(email);
                            dto.setDetalles(detalles);
                            dto.setTotal(total);
                            return dto;
                        }))
                .flatMap(dto -> webClient.post().uri(webhookUrl).bodyValue(dto).retrieve().bodyToMono(Void.class))
                .doOnSuccess(v -> log.info("Notificación enviada para carrito: {}", carrito.getIdCarrito()))
                .doOnError(e -> log.error("Error enviando notificación para carrito: {}", carrito.getIdCarrito(), e))
                .onErrorResume(e -> Mono.empty());
    }
}
