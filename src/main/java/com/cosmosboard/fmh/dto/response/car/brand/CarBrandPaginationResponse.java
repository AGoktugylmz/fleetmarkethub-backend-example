package com.cosmosboard.fmh.dto.response.car.brand;

import com.cosmosboard.fmh.dto.response.PaginationResponse;
import com.cosmosboard.fmh.entity.CarBrand;
import org.springframework.data.domain.Page;

import java.util.List;

public class CarBrandPaginationResponse extends PaginationResponse<CarBrandResponse> {
    public CarBrandPaginationResponse(Page<CarBrand> pageModel, List<CarBrandResponse> items) {
        super(pageModel, items);
    }
}
