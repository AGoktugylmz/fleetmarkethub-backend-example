package com.cosmosboard.fmh.entity;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class RevenueCatTransferData {
    @Builder.Default
    private boolean hasActiveSubscription = false;

    private LocalDateTime originalEndDate;
}