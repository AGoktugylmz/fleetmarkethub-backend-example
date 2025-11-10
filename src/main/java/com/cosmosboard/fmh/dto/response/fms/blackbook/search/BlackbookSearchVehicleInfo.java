package com.cosmosboard.fmh.dto.response.fms.blackbook.search;

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
@Schema(description = "FMS Manheim Search Vehicle Info Response DTO")
public class BlackbookSearchVehicleInfo {
    @Schema(name = "id", description = "ID", example = "9de70040-7509-4796-902f-6e02f061f45f")
    private String id;

    @Schema(name = "vin", description = "VIN", example = "1MELM55U8TA608906")
    private String vin;

    @Schema(name = "mileage", description = "Mileage", example = "2000")
    private Integer mileage;

    @Schema(name = "state", description = "State", example = "CA")
    private String state;

    @Schema(name = "grade", description = "Grade", example = "37")
    private Integer grade;
}
