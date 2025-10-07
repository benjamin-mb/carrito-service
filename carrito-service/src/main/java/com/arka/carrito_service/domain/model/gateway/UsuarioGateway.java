package com.arka.carrito_service.domain.model.gateway;
import com.arka.carrito_service.domain.model.Usuario;
import java.util.Optional;

public interface UsuarioGateway {
    Optional<Usuario> findById(Integer id);
    Boolean existsById(Integer id);
}
