package com.cosmosboard.fmh.dto.response.fms.manheim.search;

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
@Schema(description = "FMS Manheim Search Vehicle Description Response DTO")
public class ManheimSearchVehicleDescription {
    @Schema(name = "modelYear", description = "Model year", example = "2025")
    private Integer modelYear;

    @Schema(name = "brand", description = "Brand", example = "HYUNDAI")
    private String brand;

    @Schema(name = "model", description = "Model", example = "ELANTRA")
    private String model;

    @Schema(name = "trim", description = "Trim", example = "4D SEDAN GLS")
    private String trim;

    @Schema(name = "subSeries", description = "Sub Series", example = "GLS")
    private String subSeries;
}
