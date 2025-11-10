package com.cosmosboard.fmh.dto.response.car;

import com.cosmosboard.fmh.dto.response.PaginationResponse;
import com.cosmosboard.fmh.entity.Car;
import org.springframework.data.domain.Page;
import java.util.List;

public class CarPaginationResponse extends PaginationResponse<CarResponse> {
    public CarPaginationResponse(Page<Car> pageModel, List<CarResponse> items) {
        super(pageModel, items);
    }
}
