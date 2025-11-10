package com.cosmosboard.fmh.dto.response.car.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CarModelNameResponse {
    @Schema(example = "A4", description = "Model name", name = "name", type = "String")
    private String name;

    public CarModelNameResponse(String name) {
        this.name = name;
    }
}