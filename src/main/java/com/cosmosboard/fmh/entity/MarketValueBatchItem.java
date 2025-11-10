package com.cosmosboard.fmh.entity;

import com.cosmosboard.fmh.util.AppConstants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "market_value_batch_items")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarketValueBatchItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_id", nullable = false)
    private MarketValueBatch batch;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_id")
    private Car car;

    @Column(name = "vin", nullable = false)
    private String vin;

    @Column(name = "mileage", nullable = false)
    private Integer mileage;

    @Column(name = "grade", precision = 4, scale = 2)
    private Float condition;

    @Column(name = "state", length = 2)
    private String state;

    @Column(name = "region", length = 2)
    private String region;

    @Column(columnDefinition = "json")
    private String data;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private AppConstants.MarketValueBatchItemStatusEnum status = AppConstants.MarketValueBatchItemStatusEnum.WAITING;
}
