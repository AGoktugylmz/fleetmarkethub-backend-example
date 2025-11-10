package com.cosmosboard.fmh.repository.jpa;

import com.cosmosboard.fmh.entity.PrivateMarketValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PrivateMarketValueRepository extends JpaRepository<PrivateMarketValue, String> {

    List<PrivateMarketValue> findAllByCarIdAndCompanyIdOrderByCreatedAtDesc(String carId, String companyId);

    Optional<PrivateMarketValue> findByIdAndCompanyId(String id, String companyId);
}