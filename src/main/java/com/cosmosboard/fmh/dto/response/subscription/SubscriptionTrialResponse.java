package com.cosmosboard.fmh.dto.response.subscription;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionTrialResponse {
    @Schema(
        name = "id",
        description = "UUID",
        type = "String",
        example = "f49877c3-bea9-4b7b-8028-6054eee2f357"
    )
    private String id;

    @Schema(
        name = "startDate",
        description = "Start date of subscription trial",
        type = "Long",
        example = "1664491051000"
    )
    private Long startDate;

    @Schema(
        name = "startDate",
        description = "Start end of subscription trial",
        type = "Long",
        example = "1664491051000"
    )
    private Long endDate;

    @Schema(
        name = "createdAt",
        description = "Date time field of subscription trial creation",
        type = "long",
        example = "1664491051000"
    )
    private Long createdAt;

    @Schema(
        name = "updatedAt",
        description = "Date time field of subscription trial update",
        type = "long",
        example = "1664491051000"
    )
    private Long updatedAt;

    /**
     * Converts a SubscriptionTrialResponse object to a new instance.
     *
     * @param subscriptionTrial the SubscriptionTrialResponse object to convert
     * @return a new SubscriptionTrialResponse object with the same values
     */
    public static SubscriptionTrialResponse from(SubscriptionTrialResponse subscriptionTrial) {
        return SubscriptionTrialResponse.builder()
            .id(subscriptionTrial.getId())
            .startDate(subscriptionTrial.getStartDate())
            .endDate(subscriptionTrial.getEndDate())
            .createdAt(subscriptionTrial.getCreatedAt())
            .updatedAt(subscriptionTrial.getUpdatedAt())
            .build();
    }
}
