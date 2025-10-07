package com.arka.carrito_service.applicationConfig;

import com.arka.carrito_service.domain.Mapper.Mapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
@Configuration
@ComponentScan(basePackages = {
        "com.arka.carrito_service.domain.useCases",
        "com.arka.carrito_service.domain.Dto",
        "com.arka.carrito_service.domain.exception",
        "com.arka.carrito_service.domain.Mapper.Mapper",
        "com.arka.carrito_service.domain.model",
        "com.arka.carrito_service.domain.model.gateway",
        "com.arka.carrito_service.infrastructure.adapters.entity",
        "com.arka.carrito_service.infrastructure.adapters.repository",
        "com.arka.carrito_service.infrastructure.adapters.mapper",
        "com.arka.carrito_service.infrastructure.controllers"
},
        includeFilters = {
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "^.+UseCase$")
        },
        useDefaultFilters = false)
public class Config {
}
