package com.iesalixar.daw2.alvarolopez.dwese_ticket_logger_api.repositories;

import com.iesalixar.daw2.alvarolopez.dwese_ticket_logger_api.entities.Province;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProvinceRepository extends JpaRepository<Province, Long> {
    //ProvinceRepository listAllProvinces(int page);
    List<Province> findAll();
    //void save();
    //void deleteById(Long id);
    //Optional<Province> findById(Long id);
    boolean existsProvinceByCode(String id);
    @Query("SELECT COUNT(r) > 0 FROM Province r WHERE r.code = :code AND r.id != :id")
    boolean existsProvinceByCodeAndNotId(@Param("code") String code, @Param("id") Long id);

    List<Province> id(long id);

    Page<Province> findByNameContainingIgnoreCase(String search, Pageable pageable);

    Object countByNameContainingIgnoreCase(String search);
}
