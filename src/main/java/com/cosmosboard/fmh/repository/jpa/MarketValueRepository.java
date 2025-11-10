package com.cosmosboard.fmh.repository.jpa;

import com.cosmosboard.fmh.dto.annotation.UUID;
import com.cosmosboard.fmh.entity.MarketValue;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MarketValueRepository extends JpaRepository<MarketValue, UUID> {
    @Query("SELECT mv FROM MarketValue mv " +
        "WHERE mv.car.id = :carId " +
        "AND mv.createdAt = (" +
        "   SELECT MAX(mv2.createdAt) FROM MarketValue mv2 " +
        "   WHERE mv2.car.id = mv.car.id AND mv2.provider = mv.provider)")
    List<MarketValue> findLastMarketValuesForCar(@Param("carId") String carId);

    @Query("""
        SELECT mv FROM MarketValue mv
        WHERE mv.car.id IN :carIds
          AND mv.createdAt IN (
            SELECT MAX(mv2.createdAt)
            FROM MarketValue mv2
            WHERE mv2.car.id IN :carIds
            GROUP BY mv2.car.id, mv2.provider
          )
        """)
    List<MarketValue> findLastMarketValuesForCars(@Param("carIds") List<String> carIds);
}
