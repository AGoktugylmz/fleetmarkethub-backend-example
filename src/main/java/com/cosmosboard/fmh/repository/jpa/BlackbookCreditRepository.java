package com.cosmosboard.fmh.repository.jpa;

import com.cosmosboard.fmh.entity.BlackbookCredit;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BlackbookCreditRepository extends JpaRepository<BlackbookCredit, String> {
    @Query("SELECT COALESCE(SUM(b.quantity), 0) FROM BlackbookCredit b WHERE b.company.id = :companyId")
    Long getNetCreditsByCompanyId(@Param("companyId") String companyId);
}
