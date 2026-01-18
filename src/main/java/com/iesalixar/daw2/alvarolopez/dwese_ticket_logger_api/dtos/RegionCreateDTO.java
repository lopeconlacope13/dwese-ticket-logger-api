package com.iesalixar.daw2.alvarolopez.dwese_ticket_logger_api.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegionCreateDTO {
    /**
     * Codigo unico de la region
     *
     * - No puede estar vacio ('@No*/

    @NotEmpty(message = "{msg.region.code.notEmpty")
    @Size(max =2, message = "{msg.region.code.size}")
    private String code;

    /**
     * Nombre completo de la region
     *
     * -No puede estar vacion (@NotEmpty)
     * - Longitud maxima de 100 caracteres @size
     *
     * Ej: Andalucia, Cataluña, Galicia*/

    @NotEmpty(message = "{msg.region.name.notEmpty")
    @Size(max = 100, message = "{msg.region.name.size}")
    private String name;
}
