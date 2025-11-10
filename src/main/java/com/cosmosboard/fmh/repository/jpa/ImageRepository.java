package com.cosmosboard.fmh.repository.jpa;

import com.cosmosboard.fmh.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.List;

public interface ImageRepository extends JpaRepository<Image, String>, JpaSpecificationExecutor<Image> {
    List<Image> findByCarId(String carId);
}
