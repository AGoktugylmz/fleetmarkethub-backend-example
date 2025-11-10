package com.cosmosboard.fmh.dto.request.subscription;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateSubscriptionRequest {
    @Schema(
        name = "trial",
        description = "Is trial?",
        type = "boolean",
        example = "true"
    )
    @Builder.Default
    private boolean trial = false;

    @Schema(
        name = "blackbookAddon",
        description = "Is blackbook addon?",
        type = "boolean",
        example = "true"
    )
    @Builder.Default
    private boolean blackbookAddon = false;
}
