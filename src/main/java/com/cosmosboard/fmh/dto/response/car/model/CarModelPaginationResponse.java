package com.cosmosboard.fmh.dto.response.car.model;

import com.cosmosboard.fmh.dto.response.PaginationResponse;
import com.cosmosboard.fmh.entity.CarModel;
import org.springframework.data.domain.Page;
import java.util.List;

public class CarModelPaginationResponse extends PaginationResponse<CarModelResponse> {
    public CarModelPaginationResponse(Page<CarModel> pageModel, List<CarModelResponse> items) {
        super(pageModel, items);
    }
}
