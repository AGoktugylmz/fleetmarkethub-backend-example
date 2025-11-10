package com.cosmosboard.fmh.dto.response.marketValue;

import com.cosmosboard.fmh.entity.MarketValue;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.ZoneOffset;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Market Value Response DTO")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MarketValueResponse {
    @Schema(
        name = "id",
        type = "String",
        description = "ID",
        example = "0ca825ff-606d-4b8d-9d51-8021c9f46d2f"
    )
    private String id;

    @Schema(
        name = "provider",
        type = "String",
        description = "Provider",
        allowableValues = {"MANHEIM", "BLACKBOOK"},
        example = "MANHEIM"
    )
    private String provider;

    @Schema(
        name = "auction",
        type = "BigDecimal",
        description = "Auction",
        example = "13650.00"
    )
    private BigDecimal auction;

    @Schema(
        name = "above",
        type = "BigDecimal",
        description = "Above",
        example = "15000.00"
    )
    private BigDecimal above;

    @Schema(
        name = "average",
        type = "BigDecimal",
        description = "Average",
        example = "13650.00"
    )
    private BigDecimal average;

    @Schema(
        name = "below",
        type = "BigDecimal",
        description = "Below",
        example = "12350.00"
    )
    private BigDecimal below;

    @Schema(
        name = "retailAuction",
        type = "BigDecimal",
        description = "Retail auction",
        example = "13650.00"
    )
    private BigDecimal retailAuction;

    @Schema(
        name = "retailAbove",
        type = "BigDecimal",
        description = "Retail above",
        example = "15000.00"
    )
    private BigDecimal retailAbove;

    @Schema(
        name = "retailAverage",
        type = "BigDecimal",
        description = "Retail average",
        example = "13650.00"
    )
    private BigDecimal retailAverage;

    @Schema(
        name = "retailBelow",
        type = "BigDecimal",
        description = "Retail below",
        example = "12350.00"
    )
    private BigDecimal retailBelow;

    @Schema(
        name = "region",
        type = "String",
        description = "Region",
        example = "NA"
    )
    private String region;

    @Schema(
        name = "mileage",
        type = "Integer",
        description = "Mileage",
        example = "22222"
    )
    private Integer mileage;

    @Schema(
        name = "averageMileage",
        description = "Average mileage",
        example = "14529"
    )
    private Integer averageMileage;

    @Schema(
        name = "conditionGrade",
        type = "Float",
        description = "Condition grade",
        example = "3.7"
    )
    private Float conditionGrade;

    @Schema(
        name = "conditionAdjustedWholesale",
        type = "BigDecimal",
        description = "Condition Adjusted Wholesale",
        example = "14000.00"
    )
    private BigDecimal conditionAdjustedWholesale;

    @Schema(
        name = "conditionAdjustedRetail",
        type = "BigDecimal",
        description = "Condition Adjusted Retail",
        example = "15500.00"
    )
    private BigDecimal conditionAdjustedRetail;

    @Schema(
        example = "1685621520000",
        description = "Date time field of creation",
        name = "createdAt",
        type = "Long"
    )
    private Long createdAt;

    @Schema(
        example = "1685621520000",
        description = "Date time field of update",
        name = "updatedAt",
        type = "Long"
    )
    private Long updatedAt;

    public static MarketValueResponse convert(MarketValue marketValue) {
        return MarketValueResponse.builder()
            .id(marketValue.getId())
            .provider(marketValue.getProvider().name())
            .auction(marketValue.getAuction())
            .above(marketValue.getAbove())
            .average(marketValue.getAverage())
            .below(marketValue.getBelow())
            .retailAuction(marketValue.getRetailAuction())
            .retailAbove(marketValue.getRetailAbove())
            .retailAverage(marketValue.getRetailAverage())
            .retailBelow(marketValue.getRetailBelow())
            .region(marketValue.getRegion())
            .mileage(marketValue.getMileage())
            .averageMileage(marketValue.getAverageMileage())
            .conditionGrade(marketValue.getConditionGrade())
            .conditionAdjustedWholesale(marketValue.getConditionAdjustedWholesale())
            .conditionAdjustedRetail(marketValue.getConditionAdjustedRetail())
            .createdAt(marketValue.getCreatedAt().toInstant(ZoneOffset.UTC).toEpochMilli())
            .updatedAt(marketValue.getUpdatedAt().toInstant(ZoneOffset.UTC).toEpochMilli())
            .build();
    }
}
