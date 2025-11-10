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
@Schema(description = "FMS Blackbook Search Add/Deduct Response DTO")
public class BlackbookSearchAddDeductResponse {
    @Schema(name = "uoc", description = "Unique Option Code", example = "N5")
    private String uoc;

    @Schema(name = "name", description = "Option Name", example = "Navigation System")
    private String name;

    @Schema(name = "xclean", description = "Extra Clean Value", example = "300")
    private Integer xclean;

    @Schema(name = "clean", description = "Clean Value", example = "300")
    private Integer clean;

    @Schema(name = "avg", description = "Average Value", example = "300")
    private Integer avg;

    @Schema(name = "rough", description = "Rough Value", example = "300")
    private Integer rough;

    @Schema(name = "auto", description = "Auto Flag", example = "N")
    private String auto;

    @Schema(name = "resid12", description = "Residual Value at 12 Months", example = "300")
    private Integer resid12;

    @Schema(name = "resid24", description = "Residual Value at 24 Months", example = "250")
    private Integer resid24;

    @Schema(name = "resid30", description = "Residual Value at 30 Months", example = "225")
    private Integer resid30;

    @Schema(name = "resid36", description = "Residual Value at 36 Months", example = "200")
    private Integer resid36;

    @Schema(name = "resid42", description = "Residual Value at 42 Months", example = "175")
    private Integer resid42;

    @Schema(name = "resid48", description = "Residual Value at 48 Months", example = "150")
    private Integer resid48;

    @Schema(name = "resid60", description = "Residual Value at 60 Months", example = "100")
    private Integer resid60;

    @Schema(name = "resid72", description = "Residual Value at 72 Months", example = "100")
    private Integer resid72;
}
