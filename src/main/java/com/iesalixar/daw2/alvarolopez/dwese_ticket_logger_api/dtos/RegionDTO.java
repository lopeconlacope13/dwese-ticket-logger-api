package com.iesalixar.daw2.alvarolopez.dwese_ticket_logger_api.dtos;

import lombok.Getter;
import lombok.Setter;


/**
 * Clase DTO (Data Transfer Object) que representa una region.
 *
 * Esta clase se utiliza para transferir datos de una region
 * entre las capas de la aplicacion, especialmente para exponerlos
 * a través de la API sin incluir informacion innecesaria o sensible
 * */

@Getter
@Setter

public class RegionDTO {

    /**
     * Identificador unico de la region
     * Es el mismo ID que se encuentra en la entidad 'Region' de la base de datos
     */
    private Long id;

    /**
     * Codigo de la region
     * Normalmente es una cadena corta (maximo 2 caractreres) que identifica la region
     * Ejemplo: "01" para Andalucia*/
    private String code;

    /**
     * Nombre completo de la region
     * Ejemplo "Andalucía, Catqluña*/
    private String name;
}
