package com.cosmosboard.fmh.dto.response.offer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OfferStatisticsResponse {
    private long cars;

    private Map<String, OfferStatusInfo> myOffers = new HashMap<>();
}
