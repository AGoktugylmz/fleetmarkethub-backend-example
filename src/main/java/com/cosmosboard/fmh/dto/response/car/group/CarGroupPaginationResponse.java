package com.cosmosboard.fmh.dto.response.car.group;

import com.cosmosboard.fmh.dto.response.PaginationResponse;
import com.cosmosboard.fmh.entity.CarGroup;
import org.springframework.data.domain.Page;
import java.util.List;

public class CarGroupPaginationResponse extends PaginationResponse<CarGroupResponse> {
    public CarGroupPaginationResponse(Page<CarGroup> pageModel, List<CarGroupResponse> items) {
        super(pageModel, items);
    }
}
