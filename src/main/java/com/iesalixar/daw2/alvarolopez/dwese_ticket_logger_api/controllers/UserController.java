package com.iesalixar.daw2.alvarolopez.dwese_ticket_logger_api.controllers;

import com.iesalixar.daw2.alvarolopez.dwese_ticket_logger_api.dtos.UserDTO;
import com.iesalixar.daw2.alvarolopez.dwese_ticket_logger_api.mappers.UserMapper;
import com.iesalixar.daw2.alvarolopez.dwese_ticket_logger_api.services.UserService;
import com.sun.net.httpserver.Request;
import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Optional;

@Controller
@GetMapping("api/users")
public class UserController {


    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;


    @GetMapping
    public ResponseEntity<?> getUser(Authentication authentication, Request request) {
        logger.info("La informacion del usuario logueado");

        //Opcion 1
        Claims claims = (Claims) authentication.getDetails();
        Long id =  (Long) claims.get("id");

        //Opcion 2
        //String token = request.getRequestHeaders().get("Authourization").subList(7, request.getRequestHeaders().get("Authourization").size()).toString();




        try {
            UserDTO userDTO = userService.getUserById(id);
            logger.info("La informacion del usuario logueado con id {}." + id);
            return ResponseEntity.ok(userDTO);
        } catch (Exception e) {
            logger.error("Error al obtener el usuario : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
