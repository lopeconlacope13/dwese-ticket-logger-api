package com.iesalixar.daw2.alvarolopez.dwese_ticket_logger_api.services;

import com.iesalixar.daw2.alvarolopez.dwese_ticket_logger_api.dtos.RegionCreateDTO;
import com.iesalixar.daw2.alvarolopez.dwese_ticket_logger_api.dtos.RegionDTO;
import com.iesalixar.daw2.alvarolopez.dwese_ticket_logger_api.entities.Region;
import com.iesalixar.daw2.alvarolopez.dwese_ticket_logger_api.mappers.RegionMapper;
import com.iesalixar.daw2.alvarolopez.dwese_ticket_logger_api.repositories.RegionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RegionService {

    private static final Logger logger = LoggerFactory.getLogger(RegionService.class);

    @Autowired
    private RegionRepository regionRepository;

    @Autowired
    private RegionMapper regionMapper;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private FileStorageService fileStorageService;

    /**
     * Obtiene todas las regiones de la base de datos y convierte a DTOs.
     *
     * @return Lista de objetos `RegionDTO` representando todas las regiones.
     */
    public Page<RegionDTO> getAllRegions(Pageable pageable) {
        logger.info("Solicitando todas las regiones con paginación: página {}, tamaño {}",
                pageable.getPageNumber(), pageable.getPageSize());

        try {
            Page<Region> regions = regionRepository.findAll(pageable);
            logger.info("Se han encontrado {} regiones en la página actual.", regions.getNumberOfElements());
            return regions.map(regionMapper::toDTO);
        } catch (Exception e) {
            logger.error("Error al obtener la lista paginada de regiones: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Busca una región específica por su ID.
     *
     * @param id Identificador único de la región.
     * @return Un Optional que contiene un `RegionDTO` si la región existe.
     */
    public Optional<RegionDTO> getRegionById(Long id) {
        try {
            logger.info("Buscando región con ID {}", id);
            Optional<Region> region = regionRepository.findById(id);
            return region.map(regionMapper::toDTO);
        } catch (Exception e) {
            logger.error("Error al buscar región con ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Error al buscar la región.", e);
        }
    }

    /**
     * Crea una nueva región en la base de datos.
     *
     * @param regionCreateDTO DTO que contiene los datos de la región a crear.
     * @param locale Idioma para los mensajes de error.
     * @return DTO de la región creada.
     * @throws IllegalArgumentException Si el código ya existe.
     */
    public RegionDTO createRegion(RegionCreateDTO regionCreateDTO, Locale locale) {
        if (regionRepository.existsByCode(regionCreateDTO.getCode())) {
            String errorMessage = messageSource.getMessage("msg.region-controller.insert.codeExist", null, locale);
            throw new IllegalArgumentException(errorMessage);
        }

        // Se convierte a Entity para almacenar en la base de datos
        Region region = regionMapper.toEntity(regionCreateDTO);

        // Guardar imagen
        if (regionCreateDTO.getImageFile() != null && !regionCreateDTO.getImageFile().isEmpty()) {
            String fileName = fileStorageService.saveFile(regionCreateDTO.getImageFile());
            if (fileName != null) {
                region.setImage(fileName); // Guardamos el nombre en la base de datos
            }
        } else {
            logger.warn("No se recibió ninguna imagen");
        }

        Region savedRegion = regionRepository.save(region);

        // Se devuelve el DTO
        return regionMapper.toDTO(savedRegion);
    }

    /**
     * Actualiza una región existente por su ID
     *
     * @param id              Identificador de la región a actualizar.
     * @param regionCreateDTO DTO que contiene los nuevos datos de la región.
     * @return DTO de la región actualizada.
     * @throws IllegalArgumentException Si la región no existe o el código ya está en uso.
     */
    public RegionDTO updateRegion(Long id, RegionCreateDTO regionCreateDTO, Locale locale) {
        Region existingRegion = regionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("La región no existe."));

        if (regionRepository.existsRegionByCodeAndNotId(regionCreateDTO.getCode(), id)) {
            String errorMessage = messageSource.getMessage("msg.region-controller.update.codeExist", null, locale);
            throw new IllegalArgumentException(errorMessage);
        }

        existingRegion.setCode(regionCreateDTO.getCode());
        existingRegion.setName(regionCreateDTO.getName());

        // Solo si viene una nueva imagen
        if (regionCreateDTO.getImageFile() != null && !regionCreateDTO.getImageFile().isEmpty()) {

            // Si queremos reemplazar la antigua, esta se borra
            if (existingRegion.getImage() != null) {
                fileStorageService.deleteFile(existingRegion.getImage());
            }

            String fileName = fileStorageService.saveFile(regionCreateDTO.getImageFile());

            if (fileName != null) {
                existingRegion.setImage(fileName);
            }
        }

        // Si no viene ninguna imagen, no tocamos la imagen que ya estaba
        Region updatedRegion = regionRepository.save(existingRegion);
        return regionMapper.toDTO(updatedRegion);
    }

    /**
     * Elimina una región específica por su ID.
     *
     * @param id Identificador único de la región.
     * @throws IllegalArgumentException Si la región no existe
     */
    public void deleteRegion(Long id) {
        if (!regionRepository.existsById(id)) {
            throw new IllegalArgumentException("La región no existe.");
        }

        regionRepository.deleteById(id);
    }

}