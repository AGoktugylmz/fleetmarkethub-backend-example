package com.cosmosboard.fmh.dto.request.car.clas;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.Size;

@Getter
@Setter
public class UpdateCarClassRequest {
    @Size(min = 1, max = 50, message = "{min_max_length}")
    @Schema(example = "Bmw", description = "Class name", name = "name", type = "String")
    private String name;
}
