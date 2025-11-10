package com.cosmosboard.fmh.dto.response.marketValue;

import com.cosmosboard.fmh.entity.MarketValueBatch;
import com.cosmosboard.fmh.util.AppConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Market Value Batch Response DTO")
public class MarketValueBatchResponse {
    @Schema(example = "123e4567-e89b-12d3-a456-426614174000", description = "Batch ID")
    private String id;

    @Schema(example = "COMPANY_123", description = "Company ID")
    private String companyId;

    @Schema(example = "USER_456", description = "User ID")
    private String userId;

    @Schema(example = "[\"MANHEIM\", \"BLACKBOOK\"]", description = "List of providers")
    private List<String> providers;

    @Schema(example = "15000.50", description = "Amount")
    private BigDecimal amount;

    @Schema(example = "2024-01-10T12:00:00", description = "Paid At date")
    private LocalDateTime paidAt;

    @Schema(example = "2024-01-15T12:00:00", description = "Completed At date")
    private LocalDateTime completedAt;

    @Schema(example = "Success", description = "Result status")
    private AppConstants.MarketValueBatchResultEnum result;

    @Schema(example = "Process completed successfully", description = "Message")
    private String message;

    public static MarketValueBatchResponse convert(MarketValueBatch batch) {
        return MarketValueBatchResponse.builder()
                .id(batch.getId())
                .companyId(batch.getCompany().getId())
                .userId(batch.getUser().getId())
                .providers(batch.getProviders().stream()
                        .map(AppConstants.MarketValueProviderEnum::name)
                        .collect(Collectors.toList()))
                .amount(batch.getAmount())
                .paidAt(batch.getPaidAt())
                .completedAt(batch.getCompletedAt())
                .result(batch.getResult())
                .message(batch.getMessage())
                .build();
    }
}
