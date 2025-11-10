package com.cosmosboard.fmh.dto.response.subscription;

import com.cosmosboard.fmh.entity.Subscription;
import com.cosmosboard.fmh.entity.SubscriptionTrial;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionResponse {
    @Schema(
        name = "id",
        description = "UUID",
        type = "String",
        example = "f49877c3-bea9-4b7b-8028-6054eee2f357"
    )
    private String id;

    @Schema(
        name = "blackbookAddon",
        description = "Is blackbook addon?",
        type = "boolean",
        example = "true"
    )
    private boolean blackbookAddon;

    @Schema(
        name = "startDate",
        description = "Start date of subscription",
        type = "Long",
        example = "1664491051000"
    )
    private Long startDate;

    @Schema(
        name = "endDate",
        description = "End date of subscription",
        type = "Long",
        example = "1664491051000"
    )
    private Long endDate;

    @Schema(
        name = "canceledAt",
        description = "Canceled at date of subscription",
        type = "Long",
        example = "1664491051000"
    )
    private Long canceledAt;

    @Schema(
        name = "lastPaymentDate",
        description = "Last payment date of subscription",
        type = "Long",
        example = "1664491051000"
    )
    private Long lastPaymentDate;

    @Schema(
        name = "status",
        description = "Status of subscription",
        type = "String",
        example = "ACTIVE"
    )
    private String status;

    @Schema(
        name = "trial",
        description = "Trial of subscription",
        type = "SubscriptionTrialResponse",
        implementation = SubscriptionTrialResponse.class
    )
    private SubscriptionTrialResponse trial;

    @Schema(
        name = "hasAccess",
        description = "User still has access to Pro features?",
        type = "boolean",
        example = "true"
    )
    private boolean hasAccess;

    @Schema(
        name = "blackbookCredits",
        description = "Blackbook credits of subscription",
        type = "Long",
        example = "10"
    )
    @Builder.Default
    private Long blackbookCredits = 0L;

    @Schema(
        name = "createdAt",
        description = "Date time field of subscription creation",
        type = "long",
        example = "1664491051000"
    )
    private Long createdAt;

    @Schema(
        name = "updatedAt",
        description = "Date time field of subscription update",
        type = "long",
        example = "1664491051000"
    )
    private Long updatedAt;

    private String trialStatusMessage;

    /**
     * Convert subscription to subscription response.
     *
     * @param subscription     Subscription
     * @param blackbookCredits Long
     * @return SubscriptionResponse
     */
    public static SubscriptionResponse convert(Subscription subscription, Long blackbookCredits) {
        SubscriptionTrialResponse subscriptionTrialResponse = null;
        String trialStatusMessage = null;

        SubscriptionTrial trial = subscription.getTrial();
        LocalDateTime now = LocalDateTime.now();

        if (trial != null) {
            boolean trialOngoing = trial.getEndDate() != null && trial.getEndDate().isAfter(now);
            boolean trialExpired = trial.getEndDate() != null && trial.getEndDate().isBefore(now);

            subscriptionTrialResponse = SubscriptionTrialResponse.builder()
                    .id(trial.getId())
                    .startDate(trial.getStartDate() != null ? trial.getStartDate().toInstant(ZoneOffset.UTC).toEpochMilli() : null)
                    .endDate(trial.getEndDate() != null ? trial.getEndDate().toInstant(ZoneOffset.UTC).toEpochMilli() : null)
                    .createdAt(trial.getCreatedAt() != null ? trial.getCreatedAt().toInstant(ZoneOffset.UTC).toEpochMilli() : null)
                    .updatedAt(trial.getUpdatedAt() != null ? trial.getUpdatedAt().toInstant(ZoneOffset.UTC).toEpochMilli() : null)
                    .build();

            if (trialOngoing) {
                trialStatusMessage = "Trial active, expiration date: " + trial.getEndDate().toLocalDate();
            } else if (trialExpired) {
                trialStatusMessage = "Trial expired. Cannot be used again.";
            }
        } else {
            trialStatusMessage = "The trial right has never been used.";
        }

        return SubscriptionResponse.builder()
            .id(subscription.getId())
            .blackbookAddon(subscription.isBlackbookAddon())
            .startDate(subscription.getStartDate() != null ?
                subscription.getStartDate().toInstant(ZoneOffset.UTC).toEpochMilli() : null)
            .endDate(subscription.getEndDate() != null ?
                subscription.getEndDate().toInstant(ZoneOffset.UTC).toEpochMilli() : null)
            .canceledAt(subscription.getCanceledAt() != null ?
                subscription.getCanceledAt().toInstant(ZoneOffset.UTC).toEpochMilli() : null)
            .lastPaymentDate(subscription.getLastPaymentDate() != null ?
                subscription.getLastPaymentDate().toInstant(ZoneOffset.UTC).toEpochMilli() : null)
            .trial(subscriptionTrialResponse)
            .hasAccess(subscription.getEndDate() != null && subscription.getEndDate().isAfter(LocalDateTime.now()))
            .blackbookCredits(blackbookCredits)
            .status(subscription.getStatus().name())
            .trialStatusMessage(trialStatusMessage)
            .createdAt(subscription.getCreatedAt() != null ?
                subscription.getCreatedAt().toInstant(ZoneOffset.UTC).toEpochMilli() : null)
            .updatedAt(subscription.getUpdatedAt() != null ?
                subscription.getUpdatedAt().toInstant(ZoneOffset.UTC).toEpochMilli() : null)
            .build();
    }

    /**
     * Convert subscription to subscription response.
     *
     * @param subscription Subscription
     * @return SubscriptionResponse
     */
    public static SubscriptionResponse convert(Subscription subscription) {
        return convert(subscription, null);
    }
}
