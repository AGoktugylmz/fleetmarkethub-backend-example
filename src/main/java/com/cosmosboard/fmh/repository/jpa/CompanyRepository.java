package com.cosmosboard.fmh.repository.jpa;

import com.cosmosboard.fmh.entity.City;
import com.cosmosboard.fmh.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface CompanyRepository extends JpaRepository<Company, String>, JpaSpecificationExecutor<Company> {
    @Query("SELECT c FROM Company c JOIN c.locations l WHERE l.city = :city")
    List<Company> findAllByLocationCity(@Param("city") City city);

    boolean existsByName(String name);

    Company findByName(String name);
}
