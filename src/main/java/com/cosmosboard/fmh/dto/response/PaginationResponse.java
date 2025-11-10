package com.cosmosboard.fmh.dto.response;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;
import java.util.List;

@Getter
@Setter
public class PaginationResponse<T> extends BaseResponse {
    @Schema(example = "1", description = "Page", required = true, name = "page", type = "String")
    private Integer page;

    @Schema(example = "3", description = "Pages", required = true, name = "pages", type = "String")
    private Integer pages;

    @Schema(example = "10", description = "Token", required = true, name = "total", type = "String")
    private Long total;

    @ArraySchema(schema = @Schema(description = "items", required = true, type = "ListDto"))
    private List<T> items;

    public PaginationResponse(Page<?> pageModel, List<T> items) {
        this.page = pageModel.getNumber() + 1;
        this.pages = pageModel.getTotalPages();
        this.total = pageModel.getTotalElements();
        this.items = items;
    }
}
