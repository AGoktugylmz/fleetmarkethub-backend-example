package com.cosmosboard.fmh.dto.response.car;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VinMmrResponse {
    private String vin;

    private BigDecimal mmr;
}
