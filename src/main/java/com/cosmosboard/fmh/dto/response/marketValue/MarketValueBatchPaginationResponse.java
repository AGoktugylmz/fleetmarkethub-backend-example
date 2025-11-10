package com.cosmosboard.fmh.dto.response.marketValue;

import com.cosmosboard.fmh.dto.response.PaginationResponse;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;
import java.util.List;

@Getter
@Setter
public class MarketValueBatchPaginationResponse extends PaginationResponse<MarketValueBatchResponse> {
    public MarketValueBatchPaginationResponse(Page<?> pageModel, List<MarketValueBatchResponse> items) {
        super(pageModel, items);
    }
}
