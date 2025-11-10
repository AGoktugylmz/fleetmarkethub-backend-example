package com.cosmosboard.fmh.dto.request.marketValue;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarketValueBatchRequest {

    @NotBlank(message = "{not_blank}")
    @Schema(example = "515c1ceb-5dbe-4257-964f-539e9d30b7f2", description = "Company ID", name = "companyId", type = "String")
    private String companyId;

    @NotBlank(message = "{not_blank}")
    @Schema(example = "123e4567-e89b-12d3-a456-426614174000", description = "User ID", name = "userId", type = "String")
    private String userId;

    @NotNull(message = "{not_null}")
    @Schema(example = "[\"MANHEIM\", \"BLACKBOOK\"]", description = "List of providers", name = "providers", type = "List<String>")
    private List<String> providers;

    @Schema(example = "Payment for services rendered", description = "Message related to the batch", name = "message", type = "String")
    private String message;

    @NotNull(message = "{not_null}")
    @Schema(description = "List of batch items", name = "items", type = "List<MarketValueBatchItemRequest>")
    private List<MarketValueBatchItemRequest> items;
}
