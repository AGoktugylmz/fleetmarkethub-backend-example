package com.cosmosboard.fmh.dto.response.offer;

import com.cosmosboard.fmh.dto.response.PaginationResponse;
import org.springframework.data.domain.Page;
import java.util.List;

public class OfferPaginationResponse extends PaginationResponse<OfferResponse> {
    public OfferPaginationResponse(Page<?> pageModel, List<OfferResponse> items) {
        super(pageModel, items);
    }
}
