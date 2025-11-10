package com.cosmosboard.fmh.dto.request.marketValue;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarketValueBatchItemRequest {

    @NotBlank(message = "{not_blank}")
    @Schema(example = "515c1ceb-5dbe-4257-964f-539e9d30b7f2", description = "Car ID", name = "carId", type = "String")
    private String carId;

    @NotBlank(message = "{not_blank}")
    @Schema(example = "1HGCM82633A123456", description = "Vehicle Identification Number (VIN)", name = "vin", type = "String")
    private String vin;

    @NotNull(message = "{not_null}")
    @Min(value = 0, message = "{min_mileage}")
    @Schema(example = "120000", description = "Car mileage", name = "mileage", type = "Integer")
    private Integer mileage;

    @NotNull(message = "{not_null}")
    @DecimalMin(value = "0.0", inclusive = true, message = "{min_condition}")
    @DecimalMax(value = "5.0", inclusive = true, message = "{max_condition}")
    @Schema(example = "3.5", description = "Condition rating (0.0 to 5.0)", name = "condition", type = "Float")
    private Float condition;

    @NotBlank(message = "{not_blank}")
    @Schema(example = "Used", description = "State of the car (e.g., New, Used, Salvage)", name = "state", type = "String")
    private String state;

    @NotBlank(message = "{not_blank}")
    @Schema(example = "California", description = "Region or location of the car", name = "region", type = "String")
    private String region;

    @Schema(description = "Additional data related to the car", name = "data", type = "Object")
    private Object data;
}