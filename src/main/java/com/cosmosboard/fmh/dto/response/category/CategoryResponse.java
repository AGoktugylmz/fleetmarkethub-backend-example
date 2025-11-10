package com.cosmosboard.fmh.dto.response.category;

import com.cosmosboard.fmh.dto.response.BaseResponse;
import com.cosmosboard.fmh.entity.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.time.ZoneOffset;

@Getter
@Setter
@Builder
public class CategoryResponse extends BaseResponse {
    @Schema(example = "f49877c3-bea9-4b7b-8028-6054eee2f357", description = "UUID", name = "id", type = "String")
    private String id;

    @Schema(example = "Enjeksiyon", description = "Category name", name = "name", type = "String")
    private String name;

    @Schema(example = "enjeksiyon", description = "Category slug", name = "slug", type = "String")
    private String slug;

    @Schema(example = "Lorem Ipsum dolor sit amet etc.", description = "Category description", name = "description",
        type = "String")
    private String description;

    @Schema(example = "Lorem Ipsum dolor sit amet etc.", description = "Category content", name = "content",
        type = "String")
    private String content;

    @Schema(example = "true", description = "Is category active?", name = "isActive", type = "String")
    private Boolean isActive;

    @Schema(example = "2022-09-29T22:37:31", description = "Date time field of city creation", name = "createdAt", type = "long")
    private long createdAt;

    @Schema(example = "2022-09-29T22:37:31", description = "Date time field of city update", name = "updatedAt", type = "long")
    private long updatedAt;

    public static CategoryResponse convert(final Category category) {
        return CategoryResponse.builder()
            .id(category.getId())
            .name(category.getName())
            .slug(category.getSlug())
            .description(category.getDescription())
            .content(category.getContent())
            .isActive(category.getIsActive())
            .createdAt(category.getCreatedAt().toInstant(ZoneOffset.UTC).toEpochMilli())
            .updatedAt(category.getUpdatedAt().toInstant(ZoneOffset.UTC).toEpochMilli())
            .build();
    }
}
