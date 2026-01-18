package com.iesalixar.daw2.alvarolopez.dwese_ticket_logger_api.controllers;

import com.iesalixar.daw2.alvarolopez.dwese_ticket_logger_api.entities.Province;
import com.iesalixar.daw2.alvarolopez.dwese_ticket_logger_api.repositories.ProvinceRepository;
import com.iesalixar.daw2.alvarolopez.dwese_ticket_logger_api.repositories.RegionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/provinces")
public class ProvinceController {

    private static final Logger logger =
            LoggerFactory.getLogger(ProvinceController.class);
    // DAO para gestionar las operaciones de las provincias en la base de datos
    @Autowired
    private ProvinceRepository provinceRepository;

    @Autowired
    private RegionRepository regionRepository;

    /**
     * Lista todas las provincias y las pasa como atributo al modelo para que sean
     * accesibles en la vista `province.html`.
     *
     * @param model Objeto del modelo para pasar datos a la vista.
     * @return El nombre de la plantilla Thymeleaf para renderizar la lista de
     * provincias.
     */
    @GetMapping()
    public String listProvinces(@RequestParam(defaultValue = "1") int page, @RequestParam(required = false) String search, @RequestParam(required = false) String sort, Model model) {
        logger.info("Solicitando la lista de todas las provincias..." + search);
        Pageable pageable = (Pageable) PageRequest.of(page - 1, 5, getSort(sort));
        Page<Province> provinces;
        int totalPages = 0;
        if (search != null && !search.isBlank()) {
            provinces = provinceRepository.findByNameContainingIgnoreCase(search, pageable);
            totalPages = (int) Math.ceil((double) provinceRepository.countByNameContainingIgnoreCase(search) / 5);
        } else {
            provinces = provinceRepository.findAll(pageable);
            totalPages = (int) Math.ceil((double) provinceRepository.count() / 5);
        }
        logger.info("Se han cargado {} provincias.", provinces.toList().size());
        model.addAttribute("listProvinces", provinces.toList()); // Pasar la lista de provincias al modelo
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", page);
        model.addAttribute("search", search);
        model.addAttribute("sort", sort);
        return "province"; // Nombre de la plantilla Thymeleaf a renderizar
    }

    /**
     * Muestra el formulario para crear una nueva provincia.
     *
     * @param model Modelo para pasar datos a la vista.
     * @return El nombre de la plantilla Thymeleaf para el formulario.
     */
    @GetMapping("/new")
    public String showNewForm(Model model) {
        logger.info("Mostrando formulario para nueva provincia.");
        model.addAttribute("province", new Province()); // Crear un nuevo objeto Provincia
        return "province-form"; // Nombre de la plantilla Thymeleaf para el formulario
    }

    /**
     * Muestra el formulario para editar una provincia existente.
     *
     * @param id    ID de la provincia a editar.
     * @param model Modelo para pasar datos a la vista.
     * @return El nombre de la plantilla Thymeleaf para el formulario.
     */
    @GetMapping("/edit")
    public String showEditForm(@RequestParam("id") Long id, Model model) {
        logger.info("Mostrando formulario de edición para la provincia con ID {}", id);
        Province province = null;
        province = provinceRepository.getReferenceById(id);
        if (province == null) {
            logger.warn("No se encontró la provincia con ID {} ", id);
        }
        model.addAttribute("province", province);
        return "province-form"; // Nombre de la plantilla Thymeleaf para el formulario
    }

    /**
     * Inserta una nueva provincia en la base de datos.
     *
     * @param province           Objeto que contiene los datos del formulario.
     * @param redirectAttributes Atributos para mensajes flash de redirección.
     * @return Redirección a la lista de provincias.
     */
    @PostMapping("/insert")
    public String insertProvince(@ModelAttribute("province") Province province,
                                 RedirectAttributes redirectAttributes) {
        logger.info("Insertando nueva provincia con código {}", province.getCode());
        if (provinceRepository.existsProvinceByCode(province.getCode())) {
            logger.warn("El código de la provincia {} ya existe.",
                    province.getCode());
            redirectAttributes.addFlashAttribute("errorMessage", "El código  de la provincia ya existe.");
            return "redirect:/provinces/new";
        }
        provinceRepository.save(province);
        logger.info("provincia {} insertada con éxito.", province.getCode());
        return "redirect:/provinces"; // Redirigir a la lista de provincias
    }

    /**
     * Actualiza una provincia existente en la base de datos.
     *
     * @param province           Objeto que contiene los datos del formulario.
     * @param redirectAttributes Atributos para mensajes flash de redirección.
     * @return Redirección a la lista de provincias.
     */
    @PostMapping("/update")
    public String updateProvince(@ModelAttribute("province") Province province,
                                 RedirectAttributes redirectAttributes) {
        logger.info("Actualizando provincia con ID {}", province.getId());
        if (provinceRepository.existsProvinceByCodeAndNotId(province.getCode(), province.getId())) {
            logger.warn("El código de la región {} ya existe para otra región.", province.getCode());
            redirectAttributes.addFlashAttribute("errorMessage", "El código de la región ya existe para otra región.");
            return "redirect:/provinces/edit?id=" + province.getId();
        }
        provinceRepository.save(province);
        logger.info("Región con ID {} actualizada con éxito.",
                province.getId());
        return "redirect:/provinces"; // Redirigir a la lista de provincias
    }

    /**
     * Elimina una provincia de la base de datos.
     *
     * @param id                 ID de la región a eliminar.
     * @param redirectAttributes Atributos para mensajes flash de redirección.
     * @return Redirección a la lista de provincias.
     */
    @PostMapping("/delete")
    public String deleteProvince(@RequestParam("id") Long id, RedirectAttributes
            redirectAttributes) {
        logger.info("Eliminando región con ID {}", id);
        provinceRepository.deleteById(id);
        logger.info("Región con ID {} eliminada con éxito.", id);
        return "redirect:/provinces"; // Redirigir a la lista de provincias
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