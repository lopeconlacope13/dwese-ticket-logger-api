package com.iesalixar.daw2.alvarolopez.dwese_ticket_logger_api.services;

import com.iesalixar.daw2.alvarolopez.dwese_ticket_logger_api.dtos.UserDTO;
import com.iesalixar.daw2.alvarolopez.dwese_ticket_logger_api.entities.User;
import com.iesalixar.daw2.alvarolopez.dwese_ticket_logger_api.mappers.UserMapper;
import com.iesalixar.daw2.alvarolopez.dwese_ticket_logger_api.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(RegionService.class);

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserMapper userMapper;

    public Long getIdByUsername(String username) {
        userRepository.getIdByUsername(username);
        return userRepository.getIdByUsername(username);
    }

    public UserDTO getUserById(Long id) {

        Optional<User> userOpt = userRepository.findById(id);

        if (userOpt.isPresent()) {
            return userMapper.toDTO(userOpt.get());
        }
        throw new RuntimeException("El usuario con identificador " + id + " no existe");


    }



}
