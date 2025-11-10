package com.cosmosboard.fmh.dto.response.car.brand;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
@Builder
public class CarBrandNameResponse {
    @Schema(example = "Lorem", description = "Name of brand", name = "name", type = "String")
    private String name;

    public CarBrandNameResponse(String name) {
        this.name = name;
    }
}