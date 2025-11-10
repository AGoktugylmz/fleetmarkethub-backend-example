package com.cosmosboard.fmh.dto.response.employee;

import com.cosmosboard.fmh.dto.response.PaginationResponse;
import org.springframework.data.domain.Page;
import java.util.List;

public class EmployeePaginationResponse extends PaginationResponse<EmployeeResponse> {
    public EmployeePaginationResponse(Page<?> pageModel, List<EmployeeResponse> items) {
        super(pageModel, items);
    }
}
