package com.cosmosboard.fmh.dto.response.privateMarketValue;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class PrivateMarketValueResponse {
    private String id;

    private BigDecimal auction;

    private BigDecimal above;

    private BigDecimal average;

    private BigDecimal below;

    private BigDecimal retailAuction;

    private BigDecimal retailAbove;

    private BigDecimal retailAverage;

    private BigDecimal retailBelow;

    private BigDecimal conditionAdjustedWholesale;

    private BigDecimal conditionAdjustedRetail;

    private Integer mileage;

    private Integer averageMileage;

    private Float conditionGrade;

    private LocalDateTime createdAt;
}
