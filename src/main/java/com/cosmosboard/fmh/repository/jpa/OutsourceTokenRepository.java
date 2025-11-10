package com.cosmosboard.fmh.repository.jpa;

import com.cosmosboard.fmh.entity.Company;
import com.cosmosboard.fmh.entity.OutsourceToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OutsourceTokenRepository extends JpaRepository<OutsourceToken, String>, JpaSpecificationExecutor<OutsourceToken> {
    boolean existsByToken(String token);

    Optional<OutsourceToken> findByToken(String token);

    List<OutsourceToken> findAllByCompany(Company company);
}