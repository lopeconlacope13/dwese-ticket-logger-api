package com.iesalixar.daw2.alvarolopez.dwese_ticket_logger_api.controllers;
import com.iesalixar.daw2.alvarolopez.dwese_ticket_logger_api.dtos.RegionCreateDTO;
import com.iesalixar.daw2.alvarolopez.dwese_ticket_logger_api.dtos.RegionDTO;
import com.iesalixar.daw2.alvarolopez.dwese_ticket_logger_api.services.RegionService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Controlador que maneja las operaciones CRUD para la entidad `Region`.
 * Utiliza `RegionDAO` para interactuar con la base de datos.
 */
@RestController
@RequestMapping("/api/regions") // Prefijo común para todas las rutas del controlador
public class RegionController {
    private static final Logger logger = LoggerFactory.getLogger(RegionController.class);

    @Autowired
    private RegionService regionService;

    /**
     * Lista todas las regiones almacenadas en la base de datos
     *
     * @return ResponseEntity con la lista de las regiones o un error en caso de fallo
     */
    @GetMapping
    public ResponseEntity<List<RegionDTO>> getAllRegions() {
        logger.info("Solicitando la lista de todas las regiones...");
        try {
            List<RegionDTO> regionDTOs = regionService.getAllRegions();
            logger.info("Se han encontrado {} regiones.", regionDTOs.size());
            return ResponseEntity.ok(regionDTOs);
        } catch (Exception e) {
            logger.error("Error al listar las regiones: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Obtiene una región específica por su ID
     *
     * @param id ID de la región solicitada.
     * @return ResponseEntity con la región encontrada o un mensaje de error si no existe.
     */
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
     * Crea una nueva región. (Adaptado de pág. 7 del PDF)
     * @param regionCreateDTO DTO con los datos para crear la región.
     * @param locale Idioma para los mensajes de error.
     * @return ResponseEntity con la región creada o un mensaje de error.
     */
    @PostMapping(consumes = "multipart/form-data") // [cite: 163]
    public ResponseEntity<?> createRegion(@Valid @ModelAttribute RegionCreateDTO regionCreateDTO, Locale locale) {
        logger.info("Insertando nueva región con código {}", regionCreateDTO.getCode());
        try {
            RegionDTO createdRegion = regionService.createRegion(regionCreateDTO, locale);
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
     * Actualiza una región existente. (Adaptado de pág. 10 del PDF)
     * @param id ID de la región a actualizar.
     * @param regionCreateDTO DTO con los datos para actualizar la región.
     * @param locale Idioma para los mensajes de error.
     * @return ResponseEntity con la región actualizada o un mensaje de error.
     */
    @PutMapping(value = "/{id}", consumes = "multipart/form-data") // [cite: 246]
    public ResponseEntity<?> updateRegion(@PathVariable Long id, @Valid @ModelAttribute RegionCreateDTO regionCreateDTO, Locale locale) {
        logger.info("Actualizando región con ID {}", id);
        try {
            RegionDTO updatedRegion = regionService.updateRegion(id, regionCreateDTO, locale);
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
     * Elimina una región por su ID. (Adaptado de pág. 13 del PDF)
     * @param id ID de la región a eliminar.
     * @return ResponseEntity con el resultado de la operación.
     */
    @DeleteMapping("/{id}") // [cite: 317]
    public ResponseEntity<?> deleteRegion(@PathVariable Long id) {
        logger.info("Eliminando región con ID {}", id);
        try {
            regionService.deleteRegion(id);
            return ResponseEntity.ok("Región eliminada con éxito.");
        } catch (IllegalArgumentException e) {
            logger.warn("Error al eliminar la región con ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error inesperado al eliminar la región con ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar la región.");
        }
    }

    /*


     * Muestra el formulario para crear una nueva región.
     *
     * @param model Modelo para pasar datos a la vista.
     * @return El nombre de la plantilla Thymeleaf para el formulario.

    @GetMapping("/new")
    public String showNewForm(Model model) {
        logger.info("Mostrando formulario para nueva región.");
        model.addAttribute("region", new Region()); // Crear un nuevo objeto Region
        return "region-form"; // Nombre de la plantilla Thymeleaf para el formulario
    }



     * Muestra el formulario para editar una región existente.
     *
     * @param id ID de la región a editar.
     * @param model Modelo para pasar datos a la vista.
     * @return El nombre de la plantilla Thymeleaf para el formulario.

    @GetMapping("/edit")
    public String showEditForm(@RequestParam("id") Long id, Model model) {
        logger.info("Mostrando formulario de edición para la región con ID {}", id);
        Optional<Region> regionOpt = regionRepository.findById(id);
        if (!regionOpt.isPresent()) {
            logger.warn("No se encontró la región con ID {}", id);
        }
        model.addAttribute("region", regionOpt.get());
        return "region-form"; // Nombre de la plantilla Thymeleaf para el formulario
    }

    */

}