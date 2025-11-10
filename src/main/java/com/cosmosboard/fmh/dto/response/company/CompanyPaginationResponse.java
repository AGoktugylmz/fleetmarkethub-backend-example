package com.cosmosboard.fmh.dto.response.company;

import com.cosmosboard.fmh.dto.response.PaginationResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public class CompanyPaginationResponse extends PaginationResponse<CompanyResponse> {
    public CompanyPaginationResponse(Page<?> pageModel, List<CompanyResponse> items) {
        super(pageModel, items);
    }
}
