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
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Locale;
import java.util.Optional;

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
     * Obtiene todas las regiones con paginación y las convierte en una página de RegionDTO.
     * @param pageable Objeto de paginación que define la página, el tamaño y la ordenación.
     * @return Página de RegionDTO.
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
            throw new RuntimeException("Error al obtener todas las regiones.", e);
        }
    }

    /**
     * Busca una región específica por su ID.
     * @param id Identificador único de la región.
     * @return Un Optional que contiene un RegionDTO si la región existe.
     */
    public Optional<RegionDTO> getRegionById(Long id) {
        try {
            logger.info("Buscando región con ID {}", id);
            return regionRepository.findById(id).map(regionMapper::toDTO);
        } catch (Exception e) {
            logger.error("Error al buscar región con ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Error al buscar la región.", e);
        }
    }

    /**
     * Crea una nueva región en la base de datos.
     */
    public RegionDTO createRegion(RegionCreateDTO createDTO, Locale locale) {
        logger.info("Creando una nueva región con código {}", createDTO.getCode());

        // Verificar si ya existe una región con el mismo código
        if (regionRepository.existsByCode(createDTO.getCode())) {
            String errorMessage = messageSource.getMessage("msg.region-controller.insert.codeExist", null, locale);
            throw new IllegalArgumentException(errorMessage);
        }

        // Procesar la imagen si se proporciona
        String fileName = null;
        if (createDTO.getImageFile() != null && !createDTO.getImageFile().isEmpty()) {
            fileName = fileStorageService.saveFile(createDTO.getImageFile());
            if (fileName == null) {
                throw new RuntimeException("Error al guardar la imagen.");
            }
        }

        // Crear la entidad Region
        Region region = regionMapper.toEntity(createDTO);
        region.setImage(fileName);

        // Guardar la nueva región
        Region savedRegion = regionRepository.save(region);
        logger.info("Región creada exitosamente con ID {}", savedRegion.getId());

        // Convertir la entidad guardada a DTO y devolverla
        return regionMapper.toDTO(savedRegion);
    }

    /**
     * Actualiza una región existente en la base de datos.
     */
    public RegionDTO updateRegion(Long id, RegionCreateDTO updateDTO, Locale locale) {
        logger.info("Actualizando región con ID {}", id);

        // Buscar la región existente
        Region existingRegion = regionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("La región no existe."));

        // Verificar si el nombre (código) ya está en uso por otra región
        if (regionRepository.existsRegionByCodeAndNotId(updateDTO.getCode(), id)) {
            String errorMessage = messageSource.getMessage("msg.region-controller.update.codeExist", null, locale);
            throw new IllegalArgumentException(errorMessage);
        }

        // Procesar la imagen si se proporciona
        String fileName = existingRegion.getImage(); // Conservar la imagen existente por defecto
        if (updateDTO.getImageFile() != null && !updateDTO.getImageFile().isEmpty()) {
            fileName = fileStorageService.saveFile(updateDTO.getImageFile());
            if (fileName == null) {
                throw new RuntimeException("Error al guardar la nueva imagen.");
            }
        }

        // Actualizar los datos de la región
        existingRegion.setCode(updateDTO.getCode());
        existingRegion.setName(updateDTO.getName());
        existingRegion.setImage(fileName);

        // Guardar los cambios
        Region updatedRegion = regionRepository.save(existingRegion);
        logger.info("Región con ID {} actualizada exitosamente.", updatedRegion.getId());

        // Convertir la entidad actualizada a DTO y devolverla
        return regionMapper.toDTO(updatedRegion);
    }

    /**
     * Elimina una región por su ID.
     */
    public void deleteRegion(Long id) {
        logger.info("Buscando región con ID {}", id);

        // Buscar la región
        Region region = regionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("La región no existe."));

        // Eliminar la imagen asociada si existe
        if (region.getImage() != null && !region.getImage().isEmpty()) {
            fileStorageService.deleteFile(region.getImage());
            logger.info("Imagen asociada a la región con ID {} eliminada.", id);
        }

        // Eliminar la región
        regionRepository.deleteById(id);
        logger.info("Región con ID {} eliminada exitosamente.", id);
    }
}