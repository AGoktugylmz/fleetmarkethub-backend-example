package com.cosmosboard.fmh.dto.request.car.brand;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.Size;

@Getter
@Setter
public class UpdateCarBrandRequest {
    @Size(min = 1, max = 50, message = "{min_max_length}")
    @Schema(example = "Bmw", description = "Brand name", name = "name", type = "String")
    private String name;
}
