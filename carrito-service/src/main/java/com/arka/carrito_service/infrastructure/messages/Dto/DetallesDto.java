package com.arka.carrito_service.infrastructure.messages.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class DetallesDto {
    private List<DetalleReduceStockDto> items;
}
