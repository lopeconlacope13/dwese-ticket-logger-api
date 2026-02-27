package com.iesalixar.daw2.alvarolopez.dwese_ticket_logger_api;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DweseTicketLoggerApiApplication {




	public static void main(String[] args) {

        try {
            // Carga el .env y mete las variables en el sistema de Java
            Dotenv dotenv = Dotenv.configure().load();
            dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
            System.out.println("✅ .env cargado ANTES de arrancar Spring.");
        } catch (Exception e) {
            System.out.println("⚠️ No se ha encontrado el archivo .env (usando variables de entorno del sistema).");
        }

        SpringApplication.run(DweseTicketLoggerApiApplication.class, args);
	}

}
