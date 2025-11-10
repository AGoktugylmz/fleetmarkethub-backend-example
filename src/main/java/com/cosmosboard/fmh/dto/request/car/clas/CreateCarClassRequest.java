package com.cosmosboard.fmh.dto.request.car.clas;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
public class CreateCarClassRequest {
    @NotBlank(message = "{not_blank}")
    @Size(min = 3, max = 100, message = "{min_max_length}")
    @Schema(example = "BMW", description = "Car Class name", name = "name", type = "String", required = true)
    private String name;
}
