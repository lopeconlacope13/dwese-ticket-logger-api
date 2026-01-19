package com.iesalixar.daw2.alvarolopez.dwese_ticket_logger_api.controllers;

import com.iesalixar.daw2.alvarolopez.dwese_ticket_logger_api.dtos.RegionCreateDTO;
import com.iesalixar.daw2.alvarolopez.dwese_ticket_logger_api.dtos.RegionDTO;
import com.iesalixar.daw2.alvarolopez.dwese_ticket_logger_api.entities.Region;
import com.iesalixar.daw2.alvarolopez.dwese_ticket_logger_api.mappers.RegionMapper;
import com.iesalixar.daw2.alvarolopez.dwese_ticket_logger_api.repositories.RegionRepository;
import com.iesalixar.daw2.alvarolopez.dwese_ticket_logger_api.services.FileStorageService;
import com.iesalixar.daw2.alvarolopez.dwese_ticket_logger_api.services.RegionService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.swing.text.html.Option;
import javax.swing.text.html.parser.Entity;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static java.lang.StableValue.map;
import static java.util.stream.Collectors.toList;

/**
 * Controlador REST que maneja las operaciones CRUD para la entidad REGION
 *Expone endpoints para gestionar regiones mediante peticiones HTTP
 */
@RestController
@RequestMapping("/api/regions")//Prefijo comun para todas las rutas del controlador
public class RegionController {
    private static final Logger logger = LoggerFactory.getLogger(RegionController.class);

    /*
    // DAO para gestionar las operaciones de las regiones en la base de datos
    @Autowired
    private RegionRepository regionRepository;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    private RegionMapper regionMapper;
    //@Autowired
    //private Region region;
    */

    @Autowired
    private RegionService regionService;

    /**
     * Lista todas las regiones almacenadas en la base de datos
     *
     * @return ResponseEntity con la lista de regiones o un error en caso de fallo.
     */
    @GetMapping
    public ResponseEntity<List<RegionDTO>> getAllRegions() {
        logger.info("Solicitando la lista de todas las regiones...");
        try {
            List<Region> regions = regionService.getAllRegions();
            logger.info("Se han encontrado {} regiones." , regionsDTOs.size());
            return ResponseEntity.ok(regionsDTOs);
        } catch (Exception e) {

            logger.error("Error al listar las regiones: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }



    /**
     * Lista todas las regiones almacenadas en la base de datos
     *
     * @return ResponseEntity con la lista de regiones o un error en caso de fallo.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Region> getRegionById(@PathVariable Long id) {
        logger.info("Buscando region con ID {}", id);
        try {
            Optional<RegionDTO> regionDTO = regionService.getRegionById(id);
            if (regionDTO.isPresent()) {
                logger.info("Region con ID {} encontrada: {} ", id);
                return ResponseEntity.ok(regionDTO.get());
            }else {
                logger.warn("No se encontro ninguna region con ID {}", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        }catch (Exception e) {
            logger.error("Error al buscar la region con ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al buscar la región.");
        }
    }


    /**
     * Muestra el formulario para crear una nueva región.
     *
     * @param model Modelo para pasar datos a la vista.
     * @return El nombre de la plantilla Thymeleaf para el formulario.
     */
    @GetMapping("/new")
    public String showNewForm(Model model) {
        logger.info("Mostrando formulario para nueva región.");
        model.addAttribute("region", new Region()); // Crear un nuevo objeto Region
        return "region-form"; // Nombre de la plantilla Thymeleaf para el formulario
    }

    /**
     * Muestra el formulario para editar una región existente.
     *
     * @param id ID de la región a editar.
     * @param model Modelo para pasar datos a la vista.
     * @return El nombre de la plantilla Thymeleaf para el formulario.
     */
    @GetMapping("/edit")
    public String showEditForm(@RequestParam("id") Long id, Model model) {
        logger.info("Mostrando formulario de edición para la región con ID {}", id);
        Optional <Region> regionOpt = regionRepository.findById(id);
        if (regionOpt.isEmpty()) {
            logger.warn("No se encontró la región con ID {}", id);
        }
        model.addAttribute("region", regionOpt.get());
        return "region-form"; // Nombre de la plantilla Thymeleaf para el formulario
    }

    /**
     * Inserta una nueva región en la base de datos.
     *
     * @param region Objeto JSON que representa la nueva region
     * @param locale Idioma de los mensajes de error
     * @return ResponseEntity con la region creada o con un mensaje de error.
     */
    @PostMapping()
    public ResponseEntity<?> createRegion(@Valid @RequestBody RegionCreateDTO regionCreateDTO, Locale locale) {
        logger.info("Insertando nueva región con código {}", regionCreateDTO.getCode());
        try {
            return regionService.createRegion (regionCreateDTO, locale);
        } catch (Exception e) {
        }
        logger.error("Error al crear la región: {}", e.getMessage());
        return ResponseEntity.status (HttpStatus.INTERNAL_SERVER_ERROR).body("Error al crear la región.");
    }

    /**
     *
     * @param id ID de la region a actualizar
     * @param region Objeto JSON con los nuevos datos
     * @param locale Idioma de los mensajes de error
     * @return ResponseEntity con la region actualizada o un mensaje de error
     */



    @PostMapping("/{id}")
    public ResponseEntity<?> updateRegion(@PathVariable Long id, @Valid @RequestBody RegionCreateDTO regionCreateDTO, Locale locale) {
        logger.info("Actualizando región con ID {}", id);
        try {
            return regionService.updateRegion(id, regionCreateDTO, locale);
        } catch (Exception e) {
        }
        logger.error("Error al actualizar la región con ID {}: {}", id, e.getMessage());
        return ResponseEntity.status (HttpStatus.INTERNAL_SERVER_ERROR).body ("Error al actualizar la región.");
    }

    /**
     * Elimina una region específica por su ID
     * @param id ID de la region a eliminar
     * @return ResponseEntity indicando el resultado de la operacion
     */
    @PostMapping("/{id}")
    public ResponseEntity<?> deleteRegion(@PathVariable Long id) {
        logger.info("Eliminando región con ID {}", id);
        try {
            return regionService.deleteRegion(id);
        } catch (Exception e) {
            logger.error("Error al eliminar la región con ID {}: {}", id, e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body ("Error al eliminar la región.");
    }

    private Sort getSort(String sort) {
        if (sort == null) {
            return Sort.by("id").ascending();
        }
        return switch (sort) {
            case "nameAsc" -> Sort.by("name").ascending();
            case "nameDesc" -> Sort.by("name").descending();
            case "codeAsc" -> Sort.by("code").ascending();
            case "codeDesc" -> Sort.by("code").descending();
            case "idDesc" -> Sort.by("id").descending();
            default -> Sort.by("id").ascending();
        };
    }
}