package com.arka.carrito_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CarritoServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(CarritoServiceApplication.class,args);

    }
}
