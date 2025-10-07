package com.arka.carrito_service.domain.Mapper;

import com.arka.carrito_service.domain.Dto.DtoCarrito;
import com.arka.carrito_service.domain.model.Carrito;
import com.arka.carrito_service.domain.model.DetalleCarrito;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;


public class Mapper {

    public DtoCarrito carritoToDto(Carrito carrito){
        List<Integer>lista=carrito.getDetalles().stream()
                .map(DetalleCarrito::getSubtotal)
                .collect(Collectors.toList());

        Integer total=lista.stream()
                .mapToInt(Integer::intValue)
                .sum();

        DtoCarrito dtoCarrito=new DtoCarrito(
          carrito.getCreado(), carrito.getEstado(),carrito.getDetalles(),total
        );
       return  dtoCarrito;
    }
}
