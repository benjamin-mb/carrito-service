package com.arka.carrito_service.applicationConfig;

import com.arka.carrito_service.domain.Mapper.Mapper;
import com.arka.carrito_service.domain.model.gateway.*;
import com.arka.carrito_service.domain.useCases.*;
import com.arka.carrito_service.infrastructure.adapters.mapper.CarritoMapper;
import com.arka.carrito_service.infrastructure.adapters.mapper.DetalleMapper;
import com.arka.carrito_service.infrastructure.adapters.mapper.ProductoMapper;
import com.arka.carrito_service.infrastructure.adapters.mapper.UsuarioMapper;
import com.arka.carrito_service.infrastructure.adapters.repository.*;
import com.arka.carrito_service.infrastructure.messages.OrderPublisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class Config {
        @Bean
        public Mapper mapper() {
                return new Mapper();
        }

        @Bean
        public UsuarioGateway usuarioGateway(UsuarioJpaRepository usuarioJpaRepository,
                                             UsuarioMapper usuarioMapper) {
                return new UsuarioRepositoryImpl(usuarioJpaRepository, usuarioMapper);
        }

        @Bean
        public ProductoGateway productoGateway(ProductoMapper productoMapper,
                                               ProductoJpaRepository productoJpaRepository
                                               ) {
                return new ProductoRepositoryImpl(productoMapper,productoJpaRepository);
        }

        @Bean
        public CarritoGateway carritoGateway(CarritoJpaRepository carritoJpaRepository,
                                             CarritoMapper carritoMapper) {
                return new CarritoRepositoryImpl(carritoJpaRepository, carritoMapper);
        }

        @Bean
        public DetalleCarritoGateway detalleCarritoGateway(DetalleCarritoJpaRepository detalleCarritoJpaRepository,
                                                           DetalleMapper detalleMapper) {
                return new DetalleCarritoRepositoryImpl(detalleCarritoJpaRepository, detalleMapper);
        }

        @Bean
        public EventPublisherGateway eventPublisherGateway(OrderPublisher orderPublisher) {
                return new EventPublisherAdapters(orderPublisher);
        }

        @Bean
        public NotificacionGateway notificacionGateway(WebClient.Builder webClient, @Value("${notificacion.webhook.url}")String webhook, ProductoGateway productoGateway, UsuarioGateway usuarioGateway){
                return new NotifacionAdapter(webClient,webhook,productoGateway,usuarioGateway);
        }

        @Bean
        public ActualizarCantidadDeDetalleCarrito actualizarCantidadDeDetalleCarrito(CarritoGateway carritoGateway,
                                                                                     DetalleCarritoGateway detalleCarritoGateway,
                                                                                     ProductoGateway productoGateway){
                return new ActualizarCantidadDeDetalleCarrito(carritoGateway,detalleCarritoGateway,productoGateway);
        }

        @Bean
        public AgregarProductoAlCarritoUseCase agregarProductoAlCarritoUseCase(UsuarioGateway usuarioGateway,
                                                                               ProductoGateway productoGateway,CarritoGateway carritoGateway,
                                                                               DetalleCarritoGateway detalleCarritoGateway
                                                                               ){
                return new AgregarProductoAlCarritoUseCase(usuarioGateway, productoGateway, carritoGateway, detalleCarritoGateway);
        }

        @Bean
        public EliminarDetalleUseCase eliminarDetalleUseCase(DetalleCarritoGateway detalleCarritoGateway,CarritoGateway carritoGateway){
                return new EliminarDetalleUseCase(detalleCarritoGateway, carritoGateway);
        }

        @Bean
        public FinalizarCarritoUseCase finalizarCarritoUseCase(CarritoGateway carritoGateway, EventPublisherGateway eventPublisherGateway) {
                return new FinalizarCarritoUseCase(carritoGateway,eventPublisherGateway);
        }

        @Bean
        public ObtenerCarritoUseCase obtenerCarritoUseCase(CarritoGateway carritoGateway, Mapper mapper) {
                return new ObtenerCarritoUseCase(carritoGateway, mapper);
        }

        @Bean
        public NotificarCarritosAbandonadosUseCase notificarCarritosAbandonadosUseCase(CarritoGateway carritoGateway,NotificacionGateway notificacionGateway){
                return new NotificarCarritosAbandonadosUseCase(carritoGateway,notificacionGateway);
        }
}
