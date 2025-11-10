package com.cosmosboard.fmh.dto.request.category;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.Size;

@Getter
@Setter
public class UpdateCategoryRequest {
    @Size(min = 3, max = 100, message = "{min_max_length}")
    @Schema(example = "Enjeksiyon", description = "Category name", name = "name", type = "String", required = true)
    private String name;

    @Schema(example = "Enjeksiyon", description = "Category name", name = "description", type = "String")
    private String description;

    @Schema(example = "Lorem Ipsum dolor sit amet etc.", description = "Category content", name = "content",
        type = "String")
    private String content;

    @Schema(example = "true", description = "Is category active?", name = "isActive", type = "Boolean")
    private Boolean isActive;
}
