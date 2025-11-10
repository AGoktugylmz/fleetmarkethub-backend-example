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
@Schema(description = "FMS Blackbook Search Mileage Response DTO")
public class BlackbookSearchMileageResponse {
    @Schema(name = "modelYear", description = "Model Year", example = "2021")
    private String modelYear;

    @Schema(name = "rangeBegin", description = "Mileage Range Begin", example = "51001")
    private Integer rangeBegin;

    @Schema(name = "rangeEnd", description = "Mileage Range End", example = "54000")
    private Integer rangeEnd;

    @Schema(name = "xclean", description = "Extra Clean Mileage Adjustment", example = "0")
    private Integer xclean;

    @Schema(name = "clean", description = "Clean Mileage Adjustment", example = "275")
    private Integer clean;

    @Schema(name = "avg", description = "Average Mileage Adjustment", example = "525")
    private Integer avg;

    @Schema(name = "rough", description = "Rough Mileage Adjustment", example = "775")
    private Integer rough;

    @Schema(name = "finadv", description = "Finance Advance", example = "150")
    private Integer finadv;

    @Schema(name = "mileageCat", description = "Mileage Category", example = "B")
    private String mileageCat;
}
