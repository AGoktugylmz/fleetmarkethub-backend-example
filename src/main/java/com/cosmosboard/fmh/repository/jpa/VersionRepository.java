package com.cosmosboard.fmh.repository.jpa;

import com.cosmosboard.fmh.entity.Version;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface VersionRepository extends JpaRepository<Version, String>, JpaSpecificationExecutor<Version> {
    Version findTopByOrderByIdDesc();
}
