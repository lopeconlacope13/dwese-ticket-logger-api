package com.iesalixar.daw2.alvarolopez.dwese_ticket_logger_api.mappers;


import com.iesalixar.daw2.alvarolopez.dwese_ticket_logger_api.dtos.NotificationCreateDTO;
import com.iesalixar.daw2.alvarolopez.dwese_ticket_logger_api.dtos.NotificationDTO;
import com.iesalixar.daw2.alvarolopez.dwese_ticket_logger_api.entities.Notification;

import java.time.Instant;

/**
 * Mapper manual para convertir entre Notification y sus DTOs.
 */
public class NotificationMapper {

    /**
     * Convierte una entidad Notification a un DTO NotificationDTO.
     */
    public static NotificationDTO toDTO(Notification notification) {
        if (notification == null) {
            return null;
        }
        return new NotificationDTO(
                notification.getId(),
                notification.getSubject(),
                notification.getMessage(),
                notification.isRead(),
                notification.getCreatedAt()
        );
    }

    /**
     * Convierte un DTO NotificationCreateDTO a una entidad Notification.
     * Genera un nuevo ID y fecha de creación automáticamente.
     */
    public static Notification toEntity(NotificationCreateDTO notificationCreateDTO) {
        if (notificationCreateDTO == null) {
            return null;
        }
        return new Notification(
                null, // ID generado automáticamente en la base de datos
                notificationCreateDTO.getSubject(),
                notificationCreateDTO.getMessage(),
                notificationCreateDTO.isRead(),
                Instant.now() // Fecha de creación actual
        );
    }
}