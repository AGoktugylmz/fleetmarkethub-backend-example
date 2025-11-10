package com.cosmosboard.fmh.entity.specification.criteria;

import com.cosmosboard.fmh.util.AppConstants;
import lombok.Builder;
import lombok.Data;
import java.io.Serializable;
import java.util.List;

@Data
@Builder
public final class OfferCriteria implements Serializable {
    private String carId;

    private String companyId;

    private String carOwnerCompanyId;

    private Double price;

    private List<AppConstants.OfferStatusEnum> statuses;

    private String q;
}