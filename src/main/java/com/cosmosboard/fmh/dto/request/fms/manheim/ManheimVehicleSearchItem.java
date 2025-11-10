package com.cosmosboard.fmh.dto.request.fms.manheim;

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
public class ManheimVehicleSearchItem {
    @Schema(name = "vin", description = "VIN", required = true, example = "1HGCM82633A000000")
    private String vin;

    @Schema(name = "mileage", description = "Mileage", required = true, example = "100000")
    private Integer mileage;

    @Schema(name = "region", description = "Region", required = true, example = "NA")
    private String region;

    @Schema(name = "grade", description = "Grade", example = "37")
    private Integer grade;
}
