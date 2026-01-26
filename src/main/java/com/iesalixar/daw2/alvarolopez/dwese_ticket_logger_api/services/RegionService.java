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
import org.springframework.stereotype.Service;

import java.util.List;
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
    private FileStorageService fileStorageService; // Servicio necesario para las imágenes

    /**
     * Obtiene todas las regiones de la base de datos y las convierte a DTOs.
     * @return Lista de objetos RegionDTO representando todas las regiones.
     */
    public List<RegionDTO> getAllRegions() {
        try {
            logger.info("Obteniendo todas las regiones...");
            List<Region> regions = regionRepository.findAll();
            logger.info("Se encontraron {} regiones.", regions.size());
            return regions.stream()
                    .map(regionMapper::toDTO)
                    .toList();
        } catch (Exception e) {
            logger.error("Error al obtener todas las regiones: {}", e.getMessage());
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
     * (Adaptado de la pág. 6 del PDF)
     */
    public RegionDTO createRegion(RegionCreateDTO createDTO, Locale locale) {

        logger.info("Creando una nueva región con código {}", createDTO.getCode());

        // Verificar si ya existe una región con el mismo código [cite: 134]
        if (regionRepository.existsByCode(createDTO.getCode())) {
            String errorMessage = messageSource.getMessage("msg.region-controller.insert.codeExist", null, locale);
            throw new IllegalArgumentException(errorMessage);
        }

        // Procesar la imagen si se proporciona [cite: 143]
        String fileName = null;
        if (createDTO.getImageFile() != null && !createDTO.getImageFile().isEmpty()) {
            fileName = fileStorageService.saveFile(createDTO.getImageFile());
            if (fileName == null) {
                throw new RuntimeException("Error al guardar la imagen."); // [cite: 148]
            }
        }

        // Crear la entidad Region [cite: 149]
        Region region = regionMapper.toEntity(createDTO);
        region.setImage(fileName);

        // Guardar la nueva región [cite: 152]
        Region savedRegion = regionRepository.save(region);
        logger.info("Región creada exitosamente con ID {}", savedRegion.getId());

        // Convertir la entidad guardada a DTO y devolverla [cite: 155]
        return regionMapper.toDTO(savedRegion);
    }

    /**
     * Actualiza una región existente en la base de datos.
     * (Adaptado de la pág. 9 del PDF)
     */
    public RegionDTO updateRegion(Long id, RegionCreateDTO updateDTO, Locale locale) {

        logger.info("Actualizando región con ID {}", id);

        // Buscar la región existente [cite: 208]
        Region existingRegion = regionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("La región no existe."));

        // Verificar si el código ya está en uso por otra región [cite: 211]
        if (regionRepository.existsRegionByCodeAndNotId(updateDTO.getCode(), id)) {
            String errorMessage = messageSource.getMessage("msg.region-controller.update.codeExist", null, locale);
            throw new IllegalArgumentException(errorMessage);
        }

        // Procesar la imagen si se proporciona [cite: 221]
        String fileName = existingRegion.getImage(); // Conservar la imagen existente por defecto [cite: 222]
        if (updateDTO.getImageFile() != null && !updateDTO.getImageFile().isEmpty()) {
            fileName = fileStorageService.saveFile(updateDTO.getImageFile());
            if (fileName == null) {
                throw new RuntimeException("Error al guardar la nueva imagen."); // [cite: 225]
            }
        }

        // Actualizar los datos de la región [cite: 229]
        existingRegion.setCode(updateDTO.getCode());
        existingRegion.setName(updateDTO.getName());
        existingRegion.setImage(fileName);

        // Guardar los cambios [cite: 233]
        Region updatedRegion = regionRepository.save(existingRegion);
        logger.info("Región con ID {} actualizada exitosamente.", updatedRegion.getId());

        // Convertir la entidad actualizada a DTO y devolverla [cite: 236]
        return regionMapper.toDTO(updatedRegion);
    }

    /**
     * Elimina una región por su ID.
     * (Adaptado de la pág. 12 del PDF)
     * @param id ID de la región a eliminar.
     * @throws IllegalArgumentException Si la región no existe.
     */
    public void deleteRegion(Long id) {

        logger.info("Buscando región con ID {}", id);

        // Buscar la región [cite: 301]
        Region region = regionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("La región no existe."));

        // Eliminar la imagen asociada si existe [cite: 304]
        if (region.getImage() != null && !region.getImage().isEmpty()) {
            fileStorageService.deleteFile(region.getImage());
            logger.info("Imagen asociada a la región con ID {} eliminada.", id);
        }

        // Eliminar la región [cite: 309]
        regionRepository.deleteById(id);
        logger.info("Región con ID {} eliminada exitosamente.", id);
    }
}