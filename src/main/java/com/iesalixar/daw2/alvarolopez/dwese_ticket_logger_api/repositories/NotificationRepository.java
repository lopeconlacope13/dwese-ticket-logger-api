package com.iesalixar.daw2.alvarolopez.dwese_ticket_logger_api.repositories;

import com.iesalixar.daw2.alvarolopez.dwese_ticket_logger_api.entities.Notification;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
/**
 * Repositorio reactivo para gestionar las notificaciones en MongoDB.
 * Extiende ReactiveMongoRepository para aprovechar la programación reactiva.
 */

public interface NotificationRepository extends ReactiveMongoRepository<Notification, String> {
}
