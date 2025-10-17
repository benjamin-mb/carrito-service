package com.arka.carrito_service.infrastructure.scheduler;

import com.arka.carrito_service.domain.useCases.NotificarCarritosAbandonadosUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SchedulerCarritoAbandonado {


    private static final Logger log = LoggerFactory.getLogger(SchedulerCarritoAbandonado.class);

    private final NotificarCarritosAbandonadosUseCase notificarCarritosAbandonadosUseCase;

    public SchedulerCarritoAbandonado(NotificarCarritosAbandonadosUseCase notificarCarritosAbandonadosUseCase) {
        this.notificarCarritosAbandonadosUseCase = notificarCarritosAbandonadosUseCase;
    }


    @Scheduled(fixedDelay = 3600000)
    public void verificarCarritosAbandonados() {
        log.info("⏰ Iniciando verificación de carritos abandonados...");

        notificarCarritosAbandonadosUseCase.notifyUsersAboutAbandonedCar()
                .doOnSuccess(v -> log.info("✅ Verificación completada"))
                .doOnError(e -> log.error("❌ Error en verificación: {}", e.getMessage()))
                .subscribe();
    }
}
