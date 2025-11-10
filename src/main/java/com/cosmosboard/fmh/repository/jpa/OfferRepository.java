package com.cosmosboard.fmh.repository.jpa;

import com.cosmosboard.fmh.entity.Company;
import com.cosmosboard.fmh.entity.Offer;
import com.cosmosboard.fmh.util.AppConstants;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.List;
import java.util.Optional;

public interface OfferRepository extends JpaRepository<Offer, String>, JpaSpecificationExecutor<Offer> {
    Optional<Offer> findOneByIdAndCompanyId(String id, String companyId);

    List<Offer> findAllByCarIdAndCompanyId(String carId, String companyId);

    long countByCompanyAndStatus(Company company, AppConstants.OfferStatusEnum status);

    List<Offer> findByCompanyAndStatus(Company company, AppConstants.OfferStatusEnum status);
}
