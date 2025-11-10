package com.cosmosboard.fmh.dto.response.car.trim;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CarModelTrimNameResponse {
    @Schema(example = "A4", description = "Model name", name = "name", type = "String")
    private String name;

    public CarModelTrimNameResponse(String name) {
        this.name = name;
    }
}