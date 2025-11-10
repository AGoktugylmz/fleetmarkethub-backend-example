package com.cosmosboard.fmh.dto.response.fms.manheim.search;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "FMS Manheim Search Vehicle Auction ManheimSearchPricesResponse Response DTO")
public class ManheimSearchPricesResponse {
    @Schema(name = "excellent", type = "BigDecimal", description = "Excellent price", example = "4050")
    private BigDecimal excellent;

    @Schema(name = "good", type = "BigDecimal", description = "Good price", example = "3225")
    private BigDecimal good;

    @Schema(name = "fair", type = "BigDecimal", description = "Fair price", example = "2400")
    private BigDecimal fair;
}
