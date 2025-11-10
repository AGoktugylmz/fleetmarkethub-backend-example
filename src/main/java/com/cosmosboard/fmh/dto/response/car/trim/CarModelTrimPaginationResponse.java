package com.cosmosboard.fmh.dto.response.car.trim;

import com.cosmosboard.fmh.dto.response.PaginationResponse;
import com.cosmosboard.fmh.entity.CarModelTrim;
import org.springframework.data.domain.Page;
import java.util.List;

public class CarModelTrimPaginationResponse extends PaginationResponse<CarModelTrimResponse> {
    public CarModelTrimPaginationResponse(Page<CarModelTrim> pageModel, List<CarModelTrimResponse> items) {
        super(pageModel, items);
    }
}
