package com.iesalixar.daw2.alvarolopez.dwese_ticket_logger_api.controllers;

import com.iesalixar.daw2.alvarolopez.dwese_ticket_logger_api.dtos.RegionCreateDTO;
import com.iesalixar.daw2.alvarolopez.dwese_ticket_logger_api.dtos.RegionDTO;
import com.iesalixar.daw2.alvarolopez.dwese_ticket_logger_api.services.RegionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;
import java.util.Optional;

@RestController
@RequestMapping("/api/regions")
public class RegionController {

    private static final Logger logger = LoggerFactory.getLogger(RegionController.class);

    @Autowired
    private RegionService regionService;

    /**
     * Obtiene todas las regiones almacenadas en la base de datos.
     * @return Lista de regiones.
     */
    @Operation(summary = "Obtener todas las regiones", description = "Devuelve una lista de todas las regiones disponibles en el sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de regiones recuperada exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RegionDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    public ResponseEntity<Page<RegionDTO>> getAllRegions(
            @PageableDefault(size = 10, sort = "name") Pageable pageable
    ) {
        logger.info("Solicitando todas las regiones con paginación: página {}, tamaño {}",
                pageable.getPageNumber(), pageable.getPageSize());
        try {
            Page<RegionDTO> regions = regionService.getAllRegions(pageable);
            logger.info("Se han encontrado {} regiones.", regions.getTotalElements());
            return ResponseEntity.ok(regions);
        } catch (Exception e) {
            logger.error("Error al listar las regiones: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Obtiene una región específica por su ID.
     * @param id ID de la región solicitada.
     * @return Región encontrada o mensaje de error.
     */
    @Operation(summary = "Obtener una región por ID", description = "Recupera una región específica según su identificador único.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Región encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RegionDTO.class))),
            @ApiResponse(responseCode = "404", description = "Región no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getRegionById(@PathVariable Long id) {
        logger.info("Buscando región con ID {}", id);
        try {
            Optional<RegionDTO> regionDTO = regionService.getRegionById(id);
            if (regionDTO.isPresent()) {
                logger.info("Región con ID {} encontrada.", id);
                return ResponseEntity.ok(regionDTO.get());
            } else {
                logger.warn("No se encontró ninguna región con ID {}", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("La región no existe.");
            }
        } catch (Exception e) {
            logger.error("Error al buscar la región con ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al buscar la región.");
        }
    }

    /**
     * Crea una nueva región.
     * @param regionCreateDTO Datos para crear la región.
     * @param locale Idioma para los mensajes de error.
     * @return Región creada o mensaje de error.
     */
    @Operation(summary = "Crear una nueva región", description = "Permite registrar una nueva región en la base de datos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Región creada exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RegionDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos proporcionados"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<?> createRegion(@Valid @ModelAttribute RegionCreateDTO regionCreateDTO, Locale locale) {
        logger.info("Creando una nueva región con código {}", regionCreateDTO.getCode());
        try {
            RegionDTO createdRegion = regionService.createRegion(regionCreateDTO, locale);
            logger.info("Región creada exitosamente con ID {}", createdRegion.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdRegion);
        } catch (IllegalArgumentException e) {
            logger.warn("Error al crear la región: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RuntimeException e) {
            logger.error("Error al guardar la imagen: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al guardar la imagen.");
        } catch (Exception e) {
            logger.error("Error inesperado al crear la región: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al crear la región.");
        }
    }

    /**
     * Actualiza una región existente.
     * @param id ID de la región a actualizar.
     * @param regionCreateDTO Datos de actualización.
     * @param locale Idioma para los mensajes de error.
     * @return Región actualizada o mensaje de error.
     */
    @Operation(summary = "Actualizar una región", description = "Permite actualizar los datos de una región existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Región actualizada exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RegionDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    public ResponseEntity<?> updateRegion(@PathVariable Long id, @Valid @ModelAttribute RegionCreateDTO regionCreateDTO, Locale locale) {
        logger.info("Actualizando región con ID {}", id);
        try {
            RegionDTO updatedRegion = regionService.updateRegion(id, regionCreateDTO, locale);
            logger.info("Región con ID {} actualizada exitosamente.", id);
            return ResponseEntity.ok(updatedRegion);
        } catch (IllegalArgumentException e) {
            logger.warn("Error al actualizar la región con ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RuntimeException e) {
            logger.error("Error al guardar la imagen para la región con ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al guardar la imagen.");
        } catch (Exception e) {
            logger.error("Error inesperado al actualizar la región con ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar la región.");
        }
    }

    /**
     * Elimina una región por su ID.
     * @param id ID de la región a eliminar.
     * @return Resultado de la operación.
     */
    @Operation(summary = "Eliminar una región", description = "Permite eliminar una región específica de la base de datos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Región eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Región no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRegion(@PathVariable Long id) {
        logger.info("Eliminando región con ID {}", id);
        try {
            regionService.deleteRegion(id);
            logger.info("Región con ID {} eliminada exitosamente.", id);
            return ResponseEntity.ok("Región eliminada con éxito.");
        } catch (IllegalArgumentException e) {
            logger.warn("Error al eliminar la región con ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error inesperado al eliminar la región con ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar la región.");
        }
    }
}