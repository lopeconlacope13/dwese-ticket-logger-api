package com.iesalixar.daw2.alvarolopez.dwese_ticket_logger_api.mappers;

import com.iesalixar.daw2.alvarolopez.dwese_ticket_logger_api.dtos.UserDTO;
import com.iesalixar.daw2.alvarolopez.dwese_ticket_logger_api.entities.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDTO toDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        return dto;
    }

}
