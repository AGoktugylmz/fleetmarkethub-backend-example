package com.cosmosboard.fmh.dto.response.company;

import com.cosmosboard.fmh.dto.response.BaseResponse;
import com.cosmosboard.fmh.entity.Company;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CompanyResponseForMessages extends BaseResponse {
    @Schema(example = "f49877c3-bea9-4b7b-8028-6054eee2f357", description = "UUID", name = "id", type = "String")
    private String id;

    @Schema(example = "Lorem", description = "Name of Company", name = "name", type = "String")
    private String name;

    public static CompanyResponseForMessages convert(final Company company) {
        return CompanyResponseForMessages.builder()
                .id(company.getId())
                .name(company.getName())
                .build();
    }
}
