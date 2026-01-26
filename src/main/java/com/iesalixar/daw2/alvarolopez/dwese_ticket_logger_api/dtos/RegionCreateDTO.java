package com.iesalixar.daw2.alvarolopez.dwese_ticket_logger_api.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class RegionCreateDTO {
    /**
     * Codigo unico de la region
     *
     * - No puede estar vacio ('@No*/
    @NotEmpty(message = "{msg.region.code.notEmpty}")
    @Size(max = 2, message = "{msg.region.code.size}")
    private String code;

    /**
     * Nombre completo de la region
     *
     * - No puede estar vacio (@NotEmpty)
     * - Longitud maxima de 100 caracteres @Size
     *
     * Ej: Andalucia, Cataluña, Galicia*/
    @NotEmpty(message = "{msg.region.name.notEmpty}")
    @Size(max = 100, message = "{msg.region.name.size}")
    private String name;

    /**
     * Archivo de imagen asociado a la región.
     * Este campo no se almacena en BD, sino que se procesa en el servicio
     * para guardar el fichero en disco y obtener su nombre.
     */
    private MultipartFile imageFile;
}
