package com.iesalixar.daw2.alvarolopez.dwese_ticket_logger_api.controllers;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;

@RestController
@RequestMapping("/api/images") // [cite: 351]
public class FileStorageController {

    private static final Logger logger = LoggerFactory.getLogger(FileStorageController.class);

    @Value("${UPLOAD_PATH}") // Asegúrate de tener esta propiedad en application.properties
    private String uploadPath;

    @GetMapping("/{fileName:.+}") // [cite: 358]
    public ResponseEntity<Resource> getImage(@PathVariable String fileName) {
        try {
            // Construir la ruta completa del archivo
            Path filePath = Paths.get(uploadPath).resolve(fileName).normalize(); // [cite: 361]
            Resource resource = new UrlResource(filePath.toUri()); // [cite: 363]

            if (resource.exists() && resource.isReadable()) {
                logger.info("Sirviendo archivo: {}", fileName);

                // Detectar tipo de contenido (jpg, png, etc.)
                String contentType = Files.probeContentType(filePath); // [cite: 366]
                if (contentType == null) {
                    contentType = "application/octet-stream"; // [cite: 369]
                }

                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_TYPE, contentType)
                        .body(resource); // [cite: 375]
            } else {
                logger.error("El archivo {} no existe o no se puede leer.", fileName);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (IOException e) {
            logger.error("Error al servir el archivo {}: {}", fileName, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}