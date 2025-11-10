package com.cosmosboard.fmh.entity.specification.criteria;

import com.cosmosboard.fmh.entity.Company;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public final class RequestCriteria {
    private String q;

    private Boolean showMyRequests;

    private Company company;
}