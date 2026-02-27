package com.iesalixar.daw2.alvarolopez.dwese_ticket_logger_api.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuración de seguridad específica para WebSockets.
 * Se define un `SecurityFilterChain` que aplica reglas de seguridad solo a los endpoints WebSocket.
 */
@Configuration
public class WebSocketSecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter; // Filtro para autenticación JWT

    /**
     * Configura la seguridad para los WebSockets en la aplicación.
     * * @param http Objeto HttpSecurity utilizado para definir las reglas de seguridad.
     * @return SecurityFilterChain con la configuración aplicada.
     * @throws Exception En caso de error en la configuración de seguridad.
     */
    @Bean
    public SecurityFilterChain websocketSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/ws/**") // Aplicar esta configuración solo a las rutas que empiezan con /ws/
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // Permitir acceso sin autenticación a los WebSockets (la validación real ocurre en el interceptor)
                )
                .csrf(csrf -> csrf.disable()) // Deshabilitar la protección CSRF ya que WebSockets no la requieren
                // Permitir WebSockets en navegadores sin restricciones
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))
                // Aplicar el filtro JWT antes del filtro de autenticación estándar
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}