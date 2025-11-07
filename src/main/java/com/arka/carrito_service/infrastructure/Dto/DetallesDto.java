package com.arka.carrito_service.infrastructure.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class DetallesDto {
    private List<DetalleReduceStockDto> items;
}
