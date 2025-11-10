package com.cosmosboard.fmh.repository.jpa;

import com.cosmosboard.fmh.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, String>,
        JpaSpecificationExecutor<Subscription> {

    @Query("SELECT s FROM Subscription s WHERE s.company.id = :companyId AND s.endDate IS NOT NULL AND s.endDate > CURRENT_TIMESTAMP")
    Optional<Subscription> findActiveByCompanyId(@Param("companyId") String companyId);

}