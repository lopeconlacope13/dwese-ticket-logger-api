package com.iesalixar.daw2.alvarolopez.dwese_ticket_logger_api.controllers;

import com.iesalixar.daw2.alvarolopez.dwese_ticket_logger_api.dtos.AuthRequestDTO;
import com.iesalixar.daw2.alvarolopez.dwese_ticket_logger_api.dtos.AuthResponseDTO;
import com.iesalixar.daw2.alvarolopez.dwese_ticket_logger_api.dtos.TwoFactorDTO;
import com.iesalixar.daw2.alvarolopez.dwese_ticket_logger_api.entities.Role;
import com.iesalixar.daw2.alvarolopez.dwese_ticket_logger_api.entities.User;
import com.iesalixar.daw2.alvarolopez.dwese_ticket_logger_api.services.EmailService;
import com.iesalixar.daw2.alvarolopez.dwese_ticket_logger_api.services.UserService;
import com.iesalixar.daw2.alvarolopez.dwese_ticket_logger_api.utils.JwtUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador responsable de gestionar las solicitudes relacionadas con la autenticación.
 * Proporciona un endpoint para autenticar usuarios y generar un token JWT en caso de éxito.
 */
@RestController
@RequestMapping("/api/v1")
public class AuthenticationController {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    @Autowired
    private AuthenticationManager authenticationManager; // Maneja la lógica de autenticación

    @Autowired
    private JwtUtil jwtUtil; // Utilidad personalizada para manejar tokens JWT

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    /**
     * Endpoint para autenticar a un usuario. Recibe las credenciales, las valida y
     * genera un token JWT que incluye información del usuario y sus roles.
     *
     * @param authRequest Un objeto {@link AuthRequestDTO} que contiene el nombre de usuario y la contraseña.
     * @return Una respuesta HTTP con un token JWT en caso de éxito o un error en caso de fallo.
     */
    @PostMapping("/authenticate")
    public ResponseEntity<AuthResponseDTO> authenticate(@Valid @RequestBody AuthRequestDTO authRequest) {
        try {
            // Validar datos de entrada (opcional si no usas validación adicional en DTO)
            if (authRequest.getUsername() == null || authRequest.getPassword() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new AuthResponseDTO(null, "El nombre de usuario y la contraseña son obligatorios.", false));
            }

            // Intenta autenticar al usuario con las credenciales proporcionadas
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );

            // Obtiene el nombre de usuario autenticado
            String username = authentication.getName();

            // Extrae los roles del usuario autenticado desde las autoridades asignadas
            List<String> roles = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority) // Convierte cada autoridad en su representación de texto
                    .toList();
            Long id = userService.getIdByUsername(username);

            // Genera un token JWT para el usuario autenticado, incluyendo sus roles
            String token = jwtUtil.generateToken(username, roles, id, true);


            String code = generateCode();
            //generateEmail("agoncan881m@g.educaand.es", code);
            userService.saveCode(code, id);

            // Retorna una respuesta con el token JWT y un mensaje de éxito
            return ResponseEntity.ok(new AuthResponseDTO(token, "Authentication successful first part", true));
        } catch (BadCredentialsException e) {
            // Manejo de credenciales inválidas
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponseDTO(null, "Credenciales inválidas. Por favor, verifica tus datos.", false));
        } catch (Exception e) {
            // Manejo de cualquier otro error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new AuthResponseDTO(null, "Ocurrió un error inesperado. Por favor, inténtalo de nuevo más tarde.", false));
        }
    }

    @PostMapping("/twofactor")
    public ResponseEntity<AuthResponseDTO> twofactor(@Valid @RequestBody TwoFactorDTO twoFactorDTO, @RequestHeader("Authorization") String tokenHeader) {
        logger.info("Iniciando two-factor authentication" + twoFactorDTO.toString());

        // 1. Limpiamos el prefijo "Bearer "
        String token = tokenHeader.replace("Bearer ", "");

        // 2. Usamos tu servicio JWT/Utilidad para extraer el ID
        Long id = jwtUtil.extractClaim(token, claims -> claims.get("id", Long.class));

        User user =  userService.getUserById(id);
        if (user != null && StringUtils.equals(twoFactorDTO.getCode(), user.getCode())) {
            List<String> roles = user.getRoles().stream()
                    .map(Role::getName) // Ajusta según tu entidad User/Role
                    .toList();

            String tokenRenew = jwtUtil.generateToken(user.getUsername(), roles, id, false);

            userService.clear2FactorCode(id);

            return ResponseEntity.ok(new AuthResponseDTO(tokenRenew, "Autenticación exitosa", false));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new AuthResponseDTO(null, "El código enviado no es correcto.", false));
    }

    /**
     * Maneja excepciones no controladas que puedan ocurrir en el controlador.
     *
     * @param e La excepción lanzada.
     * @return Una respuesta HTTP con el mensaje de error y el estado HTTP correspondiente.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<@NotNull AuthResponseDTO> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new AuthResponseDTO(null, "Ocurrió un error inesperado: " + e.getMessage(), false));
    }

    private String generateCode(){
        return RandomStringUtils.randomNumeric(6);
    }

    private void generateEmail(String email, String code) {
        try {
            emailService.sendEmail(email, "Código de autenticación", "Su codigo de autienticación es " + code);
        }catch (Exception e) {
            logger.error(e.getLocalizedMessage());
        }
    }

}