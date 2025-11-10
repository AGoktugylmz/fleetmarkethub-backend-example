package com.cosmosboard.fmh.dto.request.car.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
public class CreateCarModelRequest {
    @NotBlank(message = "{not_blank}")
    @Size(min = 1, max = 100, message = "{min_max_length}")
    @Schema(example = "Julietta", description = "Car model name", name = "name", type = "String", required = true)
    private String name;

    @NotBlank(message = "{not_blank}")
    @Size(min = 3, max = 100, message = "{min_max_length}")
    @Schema(example = "brand-id", description = "Car brand-id name", name = "name", type = "String", required = true)
    private String brand_id;
}