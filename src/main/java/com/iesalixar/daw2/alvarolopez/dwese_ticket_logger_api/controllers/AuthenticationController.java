package com.iesalixar.daw2.alvarolopez.dwese_ticket_logger_api.controllers;

import com.iesalixar.daw2.alvarolopez.dwese_ticket_logger_api.dtos.AuthRequestDTO;
import com.iesalixar.daw2.alvarolopez.dwese_ticket_logger_api.dtos.AuthResponseDTO;
import com.iesalixar.daw2.alvarolopez.dwese_ticket_logger_api.utils.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controlador responsable de gestionar las solicitudes relacionadas con la autenticación.
 * Proporciona un endpoint para autenticar usuarios y generar un token JWT en caso de éxito.
 */
@RestController
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager; // Maneja la lógica de autenticación
    @Autowired
    private JwtUtil jwtUtil; // Utilidad personalizada para manejar tokens JWT

    /**
     * genera un token JWT que incluye informacion del usuario y sus roles
     *
     * @Param authRequest Un Objeto
     */

    @PostMapping("/authenticate")
    public ResponseEntity<AuthResponseDTO> authenticate(@Valid @RequestBody AuthRequestDTO authRequest) {
        try {
            //Validar datos de entrada (opcional si no usas validación adiccional en DTO)
            if (authRequest.getUsername() == null || authRequest.getPassword() == null){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new AuthResponseDTO("", "El nombre de usuario y la contraseña son obligatorios."));
            }
            //Intenta autenticar al usuario con las credenciales proporcionadas
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );

            //Obtiene el nombre de usuario autenticado
            String username = authentication.getName();
            //Extrae los roles del usuario autenticado desde las autoridades asignadas
            List<String> roles = authentication.getAuthorities().stream()
                    .map(authority -> authority.getAuthority())
                    .toList();
            //Genera un token JWT para el usuario autenticado, incluyendo sus roles
            String token = jwtUtil.generateToken(username, roles);
            //Retorna una respuesta con el token JWT y un mensaje de éxito
            return ResponseEntity.ok(new AuthResponseDTO(token, "Authentication successful"));
        }catch (BadCredentialsException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponseDTO(null, "Credenciales inválidas. Por favor, verifica tus datos."));
        }catch (Exception e){
            //Manejo de cualquier otro error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new AuthResponseDTO(null, "Ocurrió un error inesperado. Por favor, inténtalo de nuevo mas tarde"));
        }

    }

    /**
     * Maneja excepciones no controladas que pueden ocurrir en el controlador.
     *
     * @param e La excepcion lanzada.
     * @return Una respuesta HTTP con el mensaje de error y el estado HHTP correspondeinte
     */

    @ExceptionHandler({Exception.class})
    public ResponseEntity<AuthResponseDTO> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new AuthResponseDTO(null, "Ocurrió un error inesperado: " + e.getMessage()));
    }
}
