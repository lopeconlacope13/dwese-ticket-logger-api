package com.iesalixar.daw2.alvarolopez.dwese_ticket_logger_api.config;

import com.iesalixar.daw2.alvarolopez.dwese_ticket_logger_api.entities.Notification;
import com.iesalixar.daw2.alvarolopez.dwese_ticket_logger_api.repositories.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.time.Instant;
import java.util.UUID;

@Component
public class NotificationDataLoader implements CommandLineRunner {


    /** Método que se ejecuta al inicio de la aplicación para insertar notificaciones de ejemplo.
     *
     * @param args argumentos de la línea de comandos
     */

    private static final Logger logger = LoggerFactory.getLogger(NotificationDataLoader.class);

    @Autowired
    NotificationRepository notificationRepository;

    @Override
    public void run(String... args) {
        logger.info("Iniciando la carga de datos de notificaciones...");
        notificationRepository.deleteAll() // Limpia las notificaciones previas
                .thenMany(
                        Flux.just(
                                new Notification(UUID.randomUUID().toString(), "Precio más bajo", "Precio más bajo para el producto en el supermercado Mercadona", false, Instant.now()),
                                new Notification(UUID.randomUUID().toString(), "Producto nuevo añadido", "Se ha añadido un nuevo producto", false,
                                Instant.now()),
                                new Notification(UUID.randomUUID().toString(), "Nuevo usuario", "Se ha registrado un nuevo usuario", false, Instant.now())
                        )
                )
 .flatMap(notificationRepository::save) // Inserta las notificaciones en MongoDB
                .doOnNext(notification -> logger.info("Notificación insertada: {}", notification))
                .doOnError(error -> logger.error("Error al insertar notificación", error))
                .subscribe();
    }

}
