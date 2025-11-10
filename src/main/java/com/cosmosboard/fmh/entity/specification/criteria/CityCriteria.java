package com.cosmosboard.fmh.entity.specification.criteria;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public final class CityCriteria {
    private String q;
}
