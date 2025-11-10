package com.cosmosboard.fmh.entity.specification.criteria;

import com.cosmosboard.fmh.util.AppConstants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarketValueBatchCriteria {

    private String q;

    private AppConstants.MarketValueBatchResultEnum result;

    private LocalDateTime paidAt;

    private LocalDateTime completedAt;
}