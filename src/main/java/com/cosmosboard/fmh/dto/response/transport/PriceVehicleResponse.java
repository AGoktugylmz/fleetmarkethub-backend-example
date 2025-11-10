package com.cosmosboard.fmh.dto.response.transport;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriceVehicleResponse {

    @Schema(example = "Car", description = "Vehicle type (case-sensitive)", name = "vehicle_type", type = "String")
    private String vehicle_type;

    @Schema(example = "Toyota", description = "Vehicle make (nullable)", name = "vehicle_make", type = "String", nullable = true)
    private String vehicle_make;

    @Schema(example = "Corolla", description = "Vehicle model (nullable)", name = "vehicle_model", type = "String", nullable = true)
    private String vehicle_model;

    @Schema(example = "2019", description = "Vehicle model year (nullable)", name = "vehicle_model_year", type = "Integer", nullable = true)
    private Integer vehicle_model_year;

    @Schema(example = "1HGCM82633A004352", description = "Vehicle VIN (nullable)", name = "vehicle_vin", type = "String", nullable = true)
    private String vehicle_vin;
}