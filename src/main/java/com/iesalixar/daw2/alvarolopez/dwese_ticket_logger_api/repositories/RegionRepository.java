package com.iesalixar.daw2.alvarolopez.dwese_ticket_logger_api.repositories;

import com.iesalixar.daw2.alvarolopez.dwese_ticket_logger_api.entities.Region;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RegionRepository extends JpaRepository<Region, Long> {
    //RegionDTO listAllRegions(int page)

    @Query("SELECT COUNT(r) > 0 FROM Region r WHERE r.code = :code AND r.id != :id")
    boolean existsRegionByCodeAndNotId(@Param("code") String code, @Param("id") Long id);

    Page<Region> findByNameContainingIgnoreCase(String search, Pageable pageable);

    Object countByNameContainingIgnoreCase(String search);

    Long id(Long id);

    boolean existsByCode(@NotEmpty(message = "{msg.region.code.notEmpty}") @Size(max = 2, message = "{msg.region.code.size}") String code);
}