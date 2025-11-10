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
@Schema(description = "FMS Manheim Search Vehicle Item Response DTO")
public class ManheimSearchVehicleItem {
    @Schema(name = "averageMileage", description = "Average mileage", example = "10000")
    private Integer averageMileage;

    @Schema(name = "description", description = "Description", implementation = ManheimSearchVehicleDescription.class)
    private ManheimSearchVehicleDescription description;

    @Schema(name = "auction", description = "Auction prices", implementation = ManheimSearchPricesResponse.class)
    private ManheimSearchPricesResponse auction;

    @Schema(name = "retail", description = "Retail prices", implementation = ManheimSearchPricesResponse.class)
    private ManheimSearchPricesResponse retail;

    @Schema(name = "conditionGrade", description = "Condition grade", example = "3.7")
    private Float conditionGrade;

    @Schema(name = "averageGrade", description = "Average grade", example = "37")
    private Integer averageGrade;

    @Schema(name = "bestMatch", description = "Best match", example = "true")
    private Boolean bestMatch;
}
