package com.cosmosboard.fmh.dto.response.car.cclass;

import com.cosmosboard.fmh.dto.response.PaginationResponse;
import com.cosmosboard.fmh.entity.CarClass;
import org.springframework.data.domain.Page;
import java.util.List;

public class CarClassPaginationResponse extends PaginationResponse<CarClassResponse> {
    public CarClassPaginationResponse(Page<CarClass> pageModel, List<CarClassResponse> items) {
        super(pageModel, items);
    }
}
