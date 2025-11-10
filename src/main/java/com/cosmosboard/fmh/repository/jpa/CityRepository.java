package com.cosmosboard.fmh.repository.jpa;

import com.cosmosboard.fmh.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CityRepository extends JpaRepository<City, String>, JpaSpecificationExecutor<City> {
    boolean existsByNameOrCode(String name, String code);

    Optional<City> findByName(String name);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM City c WHERE " +
        "(c.name = :name OR c.code = :code) AND c.id <> :id")
    boolean existByNameOrCodeAndIdNotIdNot(@Param("name") String name, @Param("code") String code,
                                           @Param("id") String id);
}
