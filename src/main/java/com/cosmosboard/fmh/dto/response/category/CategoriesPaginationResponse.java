package com.cosmosboard.fmh.dto.response.category;

import com.cosmosboard.fmh.dto.response.PaginationResponse;
import org.springframework.data.domain.Page;
import java.util.List;

public class CategoriesPaginationResponse extends PaginationResponse<CategoryResponse> {
    public CategoriesPaginationResponse(Page<?> pageModel, List<CategoryResponse> items) {
        super(pageModel, items);
    }
}
